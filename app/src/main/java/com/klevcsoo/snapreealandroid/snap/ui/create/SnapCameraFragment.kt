package com.klevcsoo.snapreealandroid.snap.ui.create

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FileOutputOptions
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
import com.klevcsoo.snapreealandroid.R
import com.klevcsoo.snapreealandroid.databinding.FragmentSnapCameraBinding
import com.klevcsoo.snapreealandroid.diary.dto.DiaryDay
import com.klevcsoo.snapreealandroid.util.serializable
import java.io.File
import kotlin.math.roundToInt

class SnapCameraFragment : Fragment() {
    private var _binding: FragmentSnapCameraBinding? = null
    private val binding get() = _binding!!

    private lateinit var diaryDay: DiaryDay

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var camera: Camera
    private lateinit var preview: Preview
    private lateinit var videoCapture: VideoCapture<Recorder>
    private var recording: Recording? = null
    private var cameraLensFacing = CameraSelector.LENS_FACING_BACK
    private var targetFile: File? = null

    private val recordingListener = Consumer<VideoRecordEvent> { event ->
        when (event) {
            is VideoRecordEvent.Start -> {
                Log.d(TAG, "Video capture started")
                binding.captureButton.icon = AppCompatResources
                    .getDrawable(requireContext(), R.drawable.rounded_stop_48)
            }

            is VideoRecordEvent.Status -> {
                val percent = event.recordingStats.recordedDurationNanos / MAX_MEDIA_LENGTH_NANO
                val progress = (100 - percent * 100).roundToInt()
                binding.captureProgressIndicator.progress = progress

                if (progress <= 0) stopVideoCapture()
            }

            is VideoRecordEvent.Finalize -> {
                Log.d(TAG, "Video capture stopped")
                recording?.close()
                recording = null
                binding.captureButton.icon = AppCompatResources
                    .getDrawable(requireContext(), R.drawable.rounded_fiber_manual_record_48)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            diaryDay = it.serializable<DiaryDay>(ARG_DIARY_DAY)!!
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
        val name = listOf(
            "diary", diaryDay.diary.id, "${diaryDay.day}.mp4"
        ).joinToString(File.separator)
        targetFile = File(requireContext().cacheDir, name)
        val fileOutputOptions = FileOutputOptions.Builder(targetFile!!).build()

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            (requireActivity() as CreateSnapActivity).requestRequiredPermissions()
            return
        }

        recording = videoCapture.output
            .prepareRecording(requireContext(), fileOutputOptions)
            .withAudioEnabled()
            .start(ContextCompat.getMainExecutor(requireActivity()), recordingListener)
    }

    private fun stopVideoCapture() {
        recording?.stop()

        if (recording == null) {
            (requireActivity() as CreateSnapActivity)
                .inspectSnap(targetFile!!)
        }
    }

    companion object {
        const val TAG = "SnapCameraFragment"

        private const val ARG_DIARY_DAY = "diaryDay"
        private const val MAX_MEDIA_LENGTH_NANO = 3000000000F

        fun newInstance(day: DiaryDay) = SnapCameraFragment().apply {
            arguments = Bundle().apply {
                putSerializable(ARG_DIARY_DAY, day)
            }
        }
    }
}
