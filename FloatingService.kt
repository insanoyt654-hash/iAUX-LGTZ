package com.jfx.iaux

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.webkit.WebViewAssetLoader
import kotlin.math.abs

class FloatingService : Service() {
    private lateinit var wm: WindowManager
    private var bubble: TextView? = null
    private var panel: LinearLayout? = null
    private var webView: WebView? = null
    private var panelParams: WindowManager.LayoutParams? = null
    private var bubbleParams: WindowManager.LayoutParams? = null
    private val channelId = "jfx_floating_panel"

    override fun onCreate() {
        super.onCreate()
        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        createNotificationChannel()
        startForeground(41, buildNotification())
        showBubble()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY
    override fun onBind(intent: Intent?): IBinder? = null

    private fun showBubble() {
        if (bubble != null) return
        val b = TextView(this).apply {
            text = "JFX"
            textSize = 15f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setBackgroundColor(0xEE00A86B.toInt())
            elevation = 12f
            setOnTouchListener(DragTouchListener { togglePanel() })
        }
        val size = dp(58)
        val p = WindowManager.LayoutParams(size, size, overlayType(),
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT).apply {
            gravity = Gravity.TOP or Gravity.START
            x = dp(12); y = dp(180)
        }
        wm.addView(b, p)
        bubble = b; bubbleParams = p
    }

    private fun togglePanel() {
        if (panel == null) showPanel() else hidePanel()
    }

    private fun showPanel() {
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(0xFF080B10.toInt())
            elevation = 18f
        }
        val bar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(12), 0, dp(8), 0)
            setBackgroundColor(0xFF101820.toInt())
        }
        val heading = TextView(this).apply {
            text = "iAUX LGTZ"
            textSize = 14f
            setTextColor(Color.WHITE)
            setPadding(0, dp(12), 0, dp(12))
        }
        val minimize = TextView(this).apply {
            text = "—"
            textSize = 22f
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            setPadding(dp(14), 0, dp(14), 0)
            setOnClickListener { hidePanel() }
        }
        val close = TextView(this).apply {
            text = "×"
            textSize = 25f
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            setPadding(dp(12), 0, dp(8), 0)
            setOnClickListener { hidePanel() }
        }
        bar.addView(heading, LinearLayout.LayoutParams(0, dp(44), 1f))
        bar.addView(minimize, LinearLayout.LayoutParams(dp(42), dp(44)))
        bar.addView(close, LinearLayout.LayoutParams(dp(42), dp(44)))
        val wv = WebView(this).apply {
            setBackgroundColor(Color.BLACK)
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.databaseEnabled = true
            settings.loadsImagesAutomatically = true
            settings.javaScriptCanOpenWindowsAutomatically = true
            settings.setSupportMultipleWindows(false)
            settings.allowFileAccess = false
            settings.allowContentAccess = false
            settings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_NEVER_ALLOW
            webChromeClient = WebChromeClient()
            val assetLoader = WebViewAssetLoader.Builder()
                .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this@FloatingService))
                .build()
            webViewClient = object : WebViewClient() {
                override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?) =
                    request?.url?.let { assetLoader.shouldInterceptRequest(it) }
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    val uri = request?.url ?: return false
                    val scheme = uri.scheme ?: return false
                    if (scheme == "https" || scheme == "http") return false
                    return try {
                        startActivity(Intent(Intent.ACTION_VIEW, uri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)); true
                    } catch (_: Exception) { true }
                }
            }
            loadUrl("https://appassets.androidplatform.net/assets/iAUX%20LGTZ.html")
        }
        container.addView(bar, LinearLayout.LayoutParams(-1, dp(44)))
        container.addView(wv, LinearLayout.LayoutParams(-1, 0, 1f))
        val metrics = resources.displayMetrics
        val width = (metrics.widthPixels * 0.94f).toInt().coerceAtMost(dp(520))
        val height = (metrics.heightPixels * 0.76f).toInt()
        val params = WindowManager.LayoutParams(width, height, overlayType(),
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT).apply {
            gravity = Gravity.CENTER
        }
        wm.addView(container, params)
        panel = container; panelParams = params; webView = wv
    }

    private fun hidePanel() {
        panel?.let { try { wm.removeView(it) } catch (_: Exception) {} }
        panel = null; panelParams = null
        webView?.destroy(); webView = null
    }

    private fun overlayType() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE

    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Burbuja iAUX LGTZ", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    private fun buildNotification(): Notification = NotificationCompat.Builder(this, channelId)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle("iAUX LGTZ está activo")
        .setContentText("La burbuja flotante está disponible")
        .setOngoing(true)
        .build()

    override fun onDestroy() {
        hidePanel()
        bubble?.let { try { wm.removeView(it) } catch (_: Exception) {} }
        bubble = null
        super.onDestroy()
    }

    private inner class DragTouchListener(private val onClick: () -> Unit) : View.OnTouchListener {
        private var downX = 0f; private var downY = 0f
        private var startX = 0; private var startY = 0
        private var moved = false
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            val params = bubbleParams ?: return false
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    downX = event.rawX; downY = event.rawY
                    startX = params.x; startY = params.y; moved = false
                    return true
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = (event.rawX - downX).toInt(); val dy = (event.rawY - downY).toInt()
                    if (abs(dx) > dp(5) || abs(dy) > dp(5)) moved = true
                    params.x = startX + dx; params.y = startY + dy
                    try { wm.updateViewLayout(v, params) } catch (_: Exception) {}
                    return true
                }
                MotionEvent.ACTION_UP -> {
                    if (!moved) onClick()
                    return true
                }
            }
            return false
        }
    }
}
