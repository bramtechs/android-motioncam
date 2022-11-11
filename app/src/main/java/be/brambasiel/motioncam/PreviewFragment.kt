package be.brambasiel.motioncam

import android.hardware.Camera
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import be.brambasiel.motioncam.databinding.FragmentPreviewBinding
import com.google.android.material.snackbar.Snackbar

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class PreviewFragment : Fragment() {

    private var _binding: FragmentPreviewBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentPreviewBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Create an instance of Camera
        val camera = CameraUtils.getCameraInstance()
        val preview = camera?.let { CameraPreview(activity!!, it) }

        // Set the Preview view as the content of our activity.
        preview?.also {
            val frame: FrameLayout = binding.cameraFrame
            frame.addView(it)
        }

        binding.settingsButton.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        binding.toggleButton.setOnClickListener {
            val hasCam = context?.let { CameraUtils.hasCameraHardware(it) };
            if (hasCam != null) {
                Snackbar.make(view, "Has camera $hasCam", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            } else {
                Snackbar.make(view, "Could not determine context!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}