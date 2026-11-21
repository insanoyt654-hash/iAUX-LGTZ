package com.jfx.iaux

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(32, 32, 32, 32)
            setBackgroundColor(0xFF05080D.toInt())
        }
        val title = TextView(this).apply {
            text = "iAUX LGTZ"
            textSize = 26f
            setTextColor(0xFFFFFFFF.toInt())
            gravity = Gravity.CENTER
        }
        val info = TextView(this).apply {
            text = "Activa la burbuja flotante para abrir tu panel sobre otras aplicaciones."
            textSize = 15f
            setTextColor(0xFFCCCCCC.toInt())
            gravity = Gravity.CENTER
            setPadding(0, 18, 0, 24)
        }
        val start = Button(this).apply {
            text = "ACTIVAR BURBUJA FLOTANTE"
            setOnClickListener { activateFloating() }
        }
        val stop = Button(this).apply {
            text = "DETENER BURBUJA"
            setOnClickListener {
                stopService(Intent(this@MainActivity, FloatingService::class.java))
                Toast.makeText(this@MainActivity, "Burbuja detenida", Toast.LENGTH_SHORT).show()
            }
        }
        root.addView(title, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        root.addView(info, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        root.addView(start)
        root.addView(stop)
        setContentView(root)
    }

    private fun activateFloating() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivity(intent)
            Toast.makeText(this, "Permite ‘Mostrar sobre otras apps’ y vuelve a tocar activar.", Toast.LENGTH_LONG).show()
            return
        }
        val intent = Intent(this, FloatingService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(intent) else startService(intent)
        Toast.makeText(this, "Burbuja activada", Toast.LENGTH_SHORT).show()
        finish()
    }
}
