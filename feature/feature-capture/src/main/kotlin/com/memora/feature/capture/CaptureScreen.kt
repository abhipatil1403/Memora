package com.memora.feature.capture

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Cameraswitch
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.FlashOff
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.memora.core.ui.theme.MemoraTheme
import timber.log.Timber
import java.io.File

@Composable
fun CaptureScreen(
    onNavigateBack: () -> Unit,
    onNavigateToProcessing: (String) -> Unit,
    viewModel: CaptureViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val navigateId by viewModel.navigateToProcessing.collectAsStateWithLifecycle()
    val colors = MemoraTheme.colors
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.onEvent(CaptureEvent.PhotoCaptured(it.toString()))
        }
    }

    val cameraController = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.IMAGE_CAPTURE)
        }
    }

    LaunchedEffect(state.isFlashEnabled) {
        cameraController.enableTorch(state.isFlashEnabled)
    }

    LaunchedEffect(state.isFrontCamera) {
        cameraController.cameraSelector = if (state.isFrontCamera) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
    }

    LaunchedEffect(navigateId) {
        navigateId?.let { id -> onNavigateToProcessing(id) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Camera preview / image preview area
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1A1A2E)),
            contentAlignment = Alignment.Center
        ) {
            if (state.capturedImageUri == null) {
                if (hasCameraPermission) {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { ctx ->
                            PreviewView(ctx).apply {
                                this.controller = cameraController
                                cameraController.bindToLifecycle(lifecycleOwner)
                            }
                        }
                    )
                } else {
                    // Permission not granted state
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Outlined.CameraAlt,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.25f),
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(MemoraTheme.spacing.space4))
                        Text(
                            text = "Camera permission is required.",
                            color = Color.White.copy(alpha = 0.4f),
                            style = MemoraTheme.typography.body,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(MemoraTheme.spacing.space4))
                        Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                            Text("Grant Permission")
                        }
                    }
                }
            } else {
                // Image selected/captured — show preview
                AsyncImage(
                    model = state.capturedImageUri,
                    contentDescription = "Captured Image Preview",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = MemoraTheme.spacing.space4, vertical = MemoraTheme.spacing.space2),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            if (state.capturedImageUri == null && hasCameraPermission) {
                IconButton(onClick = { viewModel.onEvent(CaptureEvent.ToggleFlash) }) {
                    Icon(
                        if (state.isFlashEnabled) Icons.Outlined.FlashOn else Icons.Outlined.FlashOff,
                        contentDescription = "Flash",
                        tint = Color.White
                    )
                }
            }
        }

        // Bottom controls
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = MemoraTheme.spacing.space8),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visible = state.capturedImageUri == null,
                enter = fadeIn(tween(200)),
                exit = fadeOut(tween(200))
            ) {
                // Camera controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Gallery button
                    IconButton(
                        onClick = {
                            galleryLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                    ) {
                        Icon(
                            Icons.Outlined.PhotoLibrary,
                            contentDescription = "Pick from gallery",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // Shutter button
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .border(3.dp, Color.White, CircleShape)
                            .clickable(enabled = hasCameraPermission) {
                                val outputFile = File(context.cacheDir, "captured_${System.currentTimeMillis()}.jpg")
                                val outputOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()
                                val executor = ContextCompat.getMainExecutor(context)
                                
                                cameraController.takePicture(
                                    outputOptions,
                                    executor,
                                    object : ImageCapture.OnImageSavedCallback {
                                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                            val savedUri = outputFileResults.savedUri ?: Uri.fromFile(outputFile)
                                            viewModel.onEvent(CaptureEvent.PhotoCaptured(savedUri.toString()))
                                        }

                                        override fun onError(exception: ImageCaptureException) {
                                            Timber.e(exception, "Failed to capture image")
                                        }
                                    }
                                )
                            }
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(58.dp)
                                .clip(CircleShape)
                                .background(if (hasCameraPermission) Color.White else Color.Gray)
                        )
                    }

                    // Flip camera
                    IconButton(
                        onClick = { viewModel.onEvent(CaptureEvent.ToggleCamera) },
                        enabled = hasCameraPermission
                    ) {
                        Icon(
                            Icons.Outlined.Cameraswitch,
                            contentDescription = "Switch camera",
                            tint = if (hasCameraPermission) Color.White else Color.Gray,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            // Confirmation controls (after capture / pick)
            AnimatedVisibility(
                visible = state.capturedImageUri != null && !state.isProcessing,
                enter = fadeIn(tween(200)),
                exit = fadeOut(tween(200))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Retake
                    IconButton(
                        onClick = { viewModel.onEvent(CaptureEvent.RetakePhoto) },
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                    ) {
                        Icon(Icons.Outlined.Close, contentDescription = "Retake", tint = Color.White)
                    }

                    // Confirm
                    IconButton(
                        onClick = { viewModel.onEvent(CaptureEvent.ConfirmPhoto) },
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(colors.primary)
                    ) {
                        Icon(Icons.Outlined.Check, contentDescription = "Confirm", tint = Color.White)
                    }
                }
            }

            // Processing indicator
            AnimatedVisibility(visible = state.isProcessing) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = colors.primary, strokeWidth = 2.dp, modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.height(MemoraTheme.spacing.space2))
                    Text(
                        text = "Analyzing image...",
                        color = Color.White.copy(alpha = 0.6f),
                        style = MemoraTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
