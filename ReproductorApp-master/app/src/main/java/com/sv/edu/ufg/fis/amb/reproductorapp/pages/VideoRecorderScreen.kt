package com.sv.edu.ufg.fis.amb.reproductorapp.pages

import android.Manifest
import android.net.Uri
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.sv.edu.ufg.fis.amb.reproductorapp.utils.FileUtils
import java.util.concurrent.Executor

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun VideoRecorderPage(
    onVideoRecorded: (Uri) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val executor: Executor = ContextCompat.getMainExecutor(context)

    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
    )

    LaunchedEffect(Unit) {
        permissionsState.launchMultiplePermissionRequest()
    }

    if (permissionsState.allPermissionsGranted) {
        var recording by remember { mutableStateOf<Recording?>(null) }
        val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
        val recorder = remember {
            Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
                .build()
        }
        val videoCapture = remember { VideoCapture.withOutput(recorder) }
        val previewView = remember { androidx.camera.view.PreviewView(context) }

        LaunchedEffect(cameraProviderFuture) {
            val cameraProvider = cameraProviderFuture.get()
            val preview = androidx.camera.core.Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    videoCapture
                )
            } catch (e: Exception) {
                // Manejar excepción
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                factory = { previewView },
                modifier = Modifier.fillMaxSize()
            )
            IconButton(
                onClick = {
                    if (recording == null) {
                        val file = FileUtils.createVideoFile(context)
                        val outputOptions = FileOutputOptions.Builder(file).build()
                        recording = videoCapture.output.prepareRecording(context, outputOptions)
                            .withAudioEnabled()
                            .start(executor) { event ->
                                if (event is VideoRecordEvent.Finalize) {
                                    if (!event.hasError()) {
                                        onVideoRecorded(Uri.fromFile(file))
                                        Log.e("ERROR", "RECORDING SUCCESS")
                                    } else {
                                        // Manejar error
                                        Log.e("ERROR", "HUBO UN ERROR EN SALVAR")
                                    }
                                    recording = null
                                }
                            }
                    } else {
                        recording?.stop()
                        recording = null
                        Log.e("ERROR", "RECORDING NULLLLLLLLLLL")
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = if (recording == null) Icons.Default.FiberManualRecord else Icons.Default.Stop,
                    contentDescription = "Grabar",
                    tint = if (recording == null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(64.dp)
                )
            }
        }
    } else {
        // Mostrar mensaje de permisos
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Se requieren permisos para usar la cámara y el micrófono.")
        }
    }
}