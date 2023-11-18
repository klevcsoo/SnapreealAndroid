package com.klevcsoo.snapreealandroid.ui.diary.details.snaps.create

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Consumer
import androidx.fragment.app.Fragment
import com.google.common.util.concurrent.ListenableFuture
import com.klevcsoo.snapreealandroid.databinding.FragmentSnapCameraBinding
import com.klevcsoo.snapreealandroid.model.Diary
import com.klevcsoo.snapreealandroid.util.serializable
import java.time.LocalDate

class SnapCameraFragment : Fragment() {
    private var _binding: FragmentSnapCameraBinding? = null
    private val binding get() = _binding!!

    private lateinit var diary: Diary
    private lateinit var snapDate: LocalDate

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var camera: Camera
    private lateinit var preview: Preview
    private lateinit var videoCapture: VideoCapture<Recorder>
    private var recording: Recording? = null
    private var cameraLensFacing = CameraSelector.LENS_FACING_BACK

    private val recordingListener = Consumer<VideoRecordEvent> { event ->
        when (event) {
            is VideoRecordEvent.Start -> {
                Log.d(TAG, "Video capture started")
            }

            is VideoRecordEvent.Status -> {
                Log.d(TAG, "Video length: ${event.recordingStats.recordedDurationNanos}")
            }

            is VideoRecordEvent.Finalize -> {
                Log.d(TAG, "Video capture stopped")
                recording?.close()
                recording = null
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            diary = it.serializable<Diary>(ARG_DIARY)!!
            snapDate = it.serializable<LocalDate>(ARG_DATE)!!
        }

        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            bindUseCases(cameraProviderFuture.get())
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

            bindUseCases(cameraProviderFuture.get())
        }

        binding.captureButton.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> startVideoCapture()
                MotionEvent.ACTION_UP -> stopVideoCapture()
            }

            v.performClick()
            return@setOnTouchListener true
        }

        return binding.root
    }

    private fun bindUseCases(cameraProvider: ProcessCameraProvider) {
        cameraProvider.unbindAll()

        preview = Preview.Builder().build()

        val recorder = Recorder.Builder()
            .setQualitySelector(QualitySelector.from(Quality.FHD))
            .build()
        videoCapture = VideoCapture.withOutput(recorder)

        val cameraSelector: CameraSelector = CameraSelector.Builder()
            .requireLensFacing(cameraLensFacing)
            .build()

        binding.cameraPreview.scaleType = PreviewView.ScaleType.FILL_CENTER
        preview.setSurfaceProvider(binding.cameraPreview.surfaceProvider)

        try {
            camera = cameraProvider.bindToLifecycle(
                this, cameraSelector, preview, videoCapture
            )
        } catch (e: Error) {
            Log.w(TAG, "Failed to bind camera", e)
        }
    }

    private fun startVideoCapture() {
        val name = "snapreeal_${diary.name}_${snapDate.toEpochDay()}"
        val contentValues = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
        }
        val mediaStoreOutputOptions = MediaStoreOutputOptions
            .Builder(requireActivity().contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            .setContentValues(contentValues)
            .build()

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        recording = videoCapture.output
            .prepareRecording(requireContext(), mediaStoreOutputOptions)
            .withAudioEnabled()
            .start(ContextCompat.getMainExecutor(requireActivity()), recordingListener)

    }

    private fun stopVideoCapture() {
        recording?.stop()
    }

    companion object {
        const val TAG = "SnapCameraFragment"

        private const val ARG_DIARY = "diary"
        private const val ARG_DATE = "snapDate"

        fun newInstance(diary: Diary, date: LocalDate) = SnapCameraFragment().apply {
            arguments = Bundle().apply {
                putSerializable(ARG_DIARY, diary)
                putSerializable(ARG_DATE, date)
            }
        }
    }
}
