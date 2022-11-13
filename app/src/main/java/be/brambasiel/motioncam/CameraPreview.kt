package be.brambasiel.motioncam

import android.content.ContentValues.TAG
import android.content.Context
import android.hardware.Camera
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.io.IOException

/** A basic Camera preview class */
class CameraPreview(
    context: Context,
    private val camera: Camera,
) : SurfaceView(context), SurfaceHolder.Callback {

    private val mHolder: SurfaceHolder = holder.apply {
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        addCallback(this@CameraPreview)
        // deprecated setting, but required on Android versions prior to 3.0
        setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
    }

    private var listeners = emptyArray<CameraListener>()

    private var lastMotionTime = 0L;
    private var cooldown = 10 * 1000;

    override fun surfaceCreated(holder: SurfaceHolder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        camera.apply {
            try {
                setDisplayOrientation(90);
                setPreviewDisplay(holder)
                startPreview()

                this.setPreviewCallback { data, cam ->
                    //val motion = CameraMath.calculateAverage(data)
                    //Log.d(TAG, "motion $motion")
                    if (CameraMath.isMotion(data, 0.4f)) {
                        lastMotionTime = System.currentTimeMillis();
                        for (listener in listeners) {
                            listener.onMotionDetected();
                        }
                    }
                    // check expiration
                    if (lastMotionTime >= 0 && System.currentTimeMillis() - lastMotionTime > cooldown) {
                        lastMotionTime = 0;
                        for (listener in listeners) {
                            listener.onMotionExpired();
                        }
                        Log.d(TAG, "expired")
                    }
                }

            } catch (e: IOException) {
                Log.d(TAG, "Error setting camera preview: ${e.message}")
            }
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, w: Int, h: Int) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.
        if (mHolder.surface == null) {
            // preview surface does not exist
            return
        }

        // stop preview before making changes
        try {
            camera.stopPreview()
        } catch (e: Exception) {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        camera.apply {
            try {
                setPreviewDisplay(mHolder)
                startPreview()
            } catch (e: Exception) {
                Log.d(TAG, "Error starting camera preview: ${e.message}")
            }
        }
    }

    fun addListener(listener: CameraListener) {
        listeners += listener
    }
}
