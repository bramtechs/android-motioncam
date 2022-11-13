package be.brambasiel.motioncam

interface CameraListener {
    fun onMotionDetected();
    fun onMotionExpired();
}