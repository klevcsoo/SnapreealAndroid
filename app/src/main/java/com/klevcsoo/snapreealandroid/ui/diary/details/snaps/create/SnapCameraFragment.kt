package com.klevcsoo.snapreealandroid.ui.diary.details.snaps.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import com.klevcsoo.snapreealandroid.databinding.FragmentSnapCameraBinding

class SnapCameraFragment : Fragment() {
    private var _binding: FragmentSnapCameraBinding? = null
    private val binding get() = _binding!!

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var camera: Camera
    private var cameraLensFacing = CameraSelector.LENS_FACING_BACK

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSnapCameraBinding.inflate(inflater, container, false)

        binding.cameraSwitchButton.setOnClickListener {
            cameraLensFacing = if (cameraLensFacing == CameraSelector.LENS_FACING_BACK) {
                CameraSelector.LENS_FACING_FRONT
            } else {
                CameraSelector.LENS_FACING_BACK
            }

            bindPreview(cameraProviderFuture.get())
        }

        return binding.root
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        cameraProvider.unbindAll()

        val preview: Preview = Preview.Builder()
            .build()

        val cameraSelector: CameraSelector = CameraSelector.Builder()
            .requireLensFacing(cameraLensFacing)
            .build()

        binding.cameraPreview.scaleType = PreviewView.ScaleType.FILL_CENTER
        preview.setSurfaceProvider(binding.cameraPreview.surfaceProvider)

        camera = cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, preview)
    }


    companion object {

        fun newInstance() = SnapCameraFragment()
    }
}
