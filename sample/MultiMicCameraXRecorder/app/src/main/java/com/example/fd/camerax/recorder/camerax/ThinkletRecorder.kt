package com.example.fd.camerax.recorder.camerax

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.core.UseCaseGroup
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.Recorder
import androidx.camera.video.VideoCapture
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.fd.camerax.recorder.util.Logging
import java.util.concurrent.Executor

internal class ThinkletRecorder {
    fun build(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        preview: Preview?,
        videoCapture: VideoCapture<Recorder>?
    ) {
        val executor: Executor = ContextCompat.getMainExecutor(context)
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener(
            {
                val cameraProvider =
                    cameraProviderFuture.get() ?: throw RuntimeException("Failed to get camera.")
                bind(
                    lifecycleOwner = lifecycleOwner,
                    cameraProvider = cameraProvider,
                    useCaseGroup = buildUseCaseGroup(
                        videoCapture = videoCapture,
                        preview = preview
                    ),
                )
            },
            executor
        )
    }

    private fun buildUseCaseGroup(
        videoCapture: VideoCapture<Recorder>? = null,
        preview: Preview? = null
    ): UseCaseGroup {
        return UseCaseGroup.Builder()
            .apply { videoCapture?.also { addUseCase(it) } }
            .apply { preview?.also { addUseCase(it) } }
            .build()
    }

    private fun bind(
        lifecycleOwner: LifecycleOwner,
        cameraProvider: ProcessCameraProvider,
        useCaseGroup: UseCaseGroup
    ) {
        kotlin.runCatching {
            cameraProvider.unbindAll()
        }.onFailure {
            Logging.w("Failed to unbindAll")
        }

        kotlin.runCatching {
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                useCaseGroup
            )
        }.onFailure {
            Logging.e("Use case binding failed")
            return
        }
    }
}
