# JFX iAUX Floating — proyecto Android Studio

Este proyecto envuelve el archivo HTML original `iAUX LGTZ.html` en un WebView y añade una burbuja flotante nativa de Android.

## Abrir y compilar
1. Descomprime el ZIP.
2. Abre la carpeta `JFX_iAUX_Floating` desde Android Studio.
3. Espera la sincronización de Gradle.
4. Ejecuta en un teléfono Android o usa **Build > Build APK(s)**.

## Primer uso
1. Abre iAUX LGTZ.
2. Pulsa **ACTIVAR BURBUJA FLOTANTE**.
3. Android abrirá el permiso **Mostrar sobre otras apps**. Autorízalo.
4. Regresa y pulsa activar. Aparecerá la burbuja verde `JFX`; arrástrala y tócala para abrir/cerrar el panel.

## Notas
- El HTML original, el manifiesto y el service worker se incluyen sin rediseño.
- Las imágenes y recursos que el HTML carga desde Neocities/Google necesitan conexión a Internet.
- Android controla el permiso de superposición; algunas pantallas protegidas o interfaces del sistema pueden ocultar la ventana.
- Este paquete es el proyecto fuente de Android Studio, no un APK ya compilado. Para generar el APK se necesita Android Studio/Android SDK.
