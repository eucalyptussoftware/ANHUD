package com.g992.anhud

import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.Surface
import android.util.Log

object ScreenMirrorManager {
    private const val TAG = "ScreenMirrorManager"

    private var projectionIntent: Intent? = null
    private var resultCode: Int = 0
    private var mediaProjection: MediaProjection? = null
    private var projectionCallback: MediaProjection.Callback? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var densityDpi: Int = 160

    /**
     * Called with a fresh consent result (e.g. from onActivityResult). Any previous
     * projection session is torn down first — reusing a stale MediaProjection after
     * a new grant is what silently breaks mirroring on reconnect.
     */
    fun setProjectionData(code: Int, intent: Intent) {
        releaseProjection()
        resultCode = code
        projectionIntent = intent
    }

    fun hasProjectionData(): Boolean = projectionIntent != null

    fun startMirroring(context: Context, surface: Surface, width: Int, height: Int, metrics: DisplayMetrics) {
        val intent = projectionIntent ?: return

        if (mediaProjection == null) {
            // NOTE: on Android 10+ this call throws SecurityException unless a
            // foreground service declared with foregroundServiceType="mediaProjection"
            // is already running when this executes. Since this HUD mirrors while
            // another app (Waze) is in the foreground, that foreground service must
            // be started (and startForeground() called) *before* setProjectionData()/
            // startMirroring() run, or capture will never come up on API 29+.
            val projectionManager = context.applicationContext
                .getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            mediaProjection = try {
                projectionManager.getMediaProjection(resultCode, intent)
            } catch (e: SecurityException) {
                Log.e(TAG, "getMediaProjection failed - is the mediaProjection foreground service running?", e)
                null
            }
            mediaProjection?.let { registerProjectionCallback(it) }
        }

        val projection = mediaProjection ?: return

        virtualDisplay?.release()
        virtualDisplay = null

        densityDpi = metrics.densityDpi
        virtualDisplay = try {
            projection.createVirtualDisplay(
                "ANHUD_Mirror",
                width,
                height,
                densityDpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                surface,
                null,
                null
            )
        } catch (e: SecurityException) {
            Log.e(TAG, "createVirtualDisplay failed", e)
            null
        }

        if (virtualDisplay != null) {
            Log.i(TAG, "Started mirroring to surface ${width}x${height}")
        }
    }

    fun stopMirroring() {
        virtualDisplay?.release()
        virtualDisplay = null
    }

    fun isMirroring(): Boolean = virtualDisplay != null

    fun resizeMirror(width: Int, height: Int) {
        virtualDisplay?.let {
            it.resize(width, height, densityDpi)
            Log.i(TAG, "Resized virtual display to ${width}x${height}")
        }
    }

    private fun registerProjectionCallback(projection: MediaProjection) {
        val callback = object : MediaProjection.Callback() {
            override fun onStop() {
                Log.i(TAG, "MediaProjection stopped")
                // The token is now dead. Clear everything so the next
                // startMirroring()/setProjectionData() call starts clean instead
                // of silently reusing a projection that can no longer capture.
                virtualDisplay?.release()
                virtualDisplay = null
                mediaProjection = null
                projectionCallback = null
                projectionIntent = null
            }
        }
        projectionCallback = callback
        projection.registerCallback(callback, Handler(Looper.getMainLooper()))
    }

    private fun releaseProjection() {
        virtualDisplay?.release()
        virtualDisplay = null
        projectionCallback?.let { mediaProjection?.unregisterCallback(it) }
        projectionCallback = null
        mediaProjection?.stop()
        mediaProjection = null
    }
}