package com.g992.anhud

import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.util.DisplayMetrics
import android.view.Surface
import android.view.TextureView
import android.graphics.SurfaceTexture
import android.graphics.Matrix
import android.util.Log

object ScreenMirrorManager {
    private var projectionIntent: Intent? = null
    private var resultCode: Int = 0
    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    
    fun setProjectionData(code: Int, intent: Intent) {
        resultCode = code
        projectionIntent = intent
    }

    fun hasProjectionData(): Boolean = projectionIntent != null

    fun startMirroring(context: Context, surface: Surface, width: Int, height: Int, metrics: DisplayMetrics) {
        if (projectionIntent == null) return
        
        val projectionManager = context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        
        if (mediaProjection == null) {
            mediaProjection = projectionManager.getMediaProjection(resultCode, projectionIntent!!)
        }
        
        virtualDisplay?.release()
        
        mediaProjection?.let {
            it.registerCallback(object : MediaProjection.Callback() {
                override fun onStop() {
                    Log.i("ScreenMirrorManager", "MediaProjection stopped")
                }
            }, android.os.Handler(android.os.Looper.getMainLooper()))
            
            virtualDisplay = it.createVirtualDisplay(
                "ANHUD_Mirror",
                metrics.widthPixels,
                metrics.heightPixels,
                metrics.densityDpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                surface,
                null,
                null
            )
            Log.i("ScreenMirrorManager", "Started mirroring to surface ${width}x${height}")
        }
    }

    fun stopMirroring() {
        virtualDisplay?.release()
        virtualDisplay = null
    }
}
