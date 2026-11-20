package com.example.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.RotateRight
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.i18n.AppStrings
import com.example.util.ImageCropHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun ProfileCameraCropDialog(
    strings: AppStrings,
    initialImageUri: Uri? = null,
    onAvatarSaved: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var activeCropUri by remember(initialImageUri) { mutableStateOf(initialImageUri) }
    var tempCaptureFile by remember { mutableStateOf<File?>(null) }

    val cleanupTemp = {
        tempCaptureFile?.let { file ->
            if (file.exists()) file.delete()
        }
        tempCaptureFile = null
    }

    DisposableEffect(Unit) {
        onDispose { cleanupTemp() }
    }

    Dialog(
        onDismissRequest = {
            cleanupTemp()
            onDismiss()
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        BackHandler {
            if (activeCropUri != null && initialImageUri == null) {
                cleanupTemp()
                activeCropUri = null
            } else {
                cleanupTemp()
                onDismiss()
            }
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF0A0A0D),
            contentColor = Color.White
        ) {
            if (activeCropUri == null) {
                InAppCameraCaptureContent(
                    strings = strings,
                    onImageCaptured = { file ->
                        tempCaptureFile = file
                        activeCropUri = Uri.fromFile(file)
                    },
                    onGalleryImageSelected = { uri ->
                        cleanupTemp()
                        activeCropUri = uri
                    },
                    onClose = {
                        cleanupTemp()
                        onDismiss()
                    }
                )
            } else {
                ProfileImageCropperContent(
                    strings = strings,
                    imageUri = activeCropUri!!,
                    canRetake = initialImageUri == null,
                    onRetake = {
                        cleanupTemp()
                        activeCropUri = null
                    },
                    onCropSaved = { savedUri ->
                        cleanupTemp()
                        onAvatarSaved(savedUri)
                        onDismiss()
                    },
                    onClose = {
                        cleanupTemp()
                        onDismiss()
                    }
                )
            }
        }
    }
}

@Composable
private fun InAppCameraCaptureContent(
    strings: AppStrings,
    onImageCaptured: (File) -> Unit,
    onGalleryImageSelected: (Uri) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    var permissionRequestedOnce by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) onGalleryImageSelected(uri)
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission && !permissionRequestedOnce) {
            permissionRequestedOnce = true
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    var useFrontCamera by remember { mutableStateOf(true) }
    var flashMode by remember { mutableIntStateOf(ImageCapture.FLASH_MODE_AUTO) }
    var canSwitchCamera by remember { mutableStateOf(false) }
    var cameraError by remember { mutableStateOf<String?>(null) }
    var isCapturing by remember { mutableStateOf(false) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var boundProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }

    val previewView = remember {
        PreviewView(context).apply {
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            try {
                boundProvider?.unbindAll()
            } catch (_: Exception) {
            }
        }
    }

    LaunchedEffect(hasCameraPermission, useFrontCamera) {
        if (!hasCameraPermission) return@LaunchedEffect
        cameraError = null
        try {
            val provider = getCameraProvider(context)
            boundProvider = provider
            val hasFront = hasCameraSafe(provider, CameraSelector.DEFAULT_FRONT_CAMERA)
            val hasBack = hasCameraSafe(provider, CameraSelector.DEFAULT_BACK_CAMERA)
            canSwitchCamera = hasFront && hasBack

            val selector = when {
                useFrontCamera && hasFront -> CameraSelector.DEFAULT_FRONT_CAMERA
                !useFrontCamera && hasBack -> CameraSelector.DEFAULT_BACK_CAMERA
                hasBack -> CameraSelector.DEFAULT_BACK_CAMERA
                hasFront -> CameraSelector.DEFAULT_FRONT_CAMERA
                else -> {
                    cameraError = "No camera hardware detected on this device."
                    return@LaunchedEffect
                }
            }

            val preview = Preview.Builder().build().apply {
                surfaceProvider = previewView.surfaceProvider
            }
            val capture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setFlashMode(flashMode)
                .build()

            provider.unbindAll()
            provider.bindToLifecycle(lifecycleOwner, selector, preview, capture)
            imageCapture = capture
        } catch (e: Exception) {
            cameraError = e.message ?: "Unable to start camera."
        }
    }

    LaunchedEffect(flashMode, imageCapture) {
        imageCapture?.flashMode = flashMode
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("camera_close_button")
            ) {
                Icon(Icons.Default.Close, contentDescription = strings.backLabel, tint = Color.White)
            }
            Text(
                text = strings.takePhoto,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            if (hasCameraPermission && !useFrontCamera) {
                IconButton(
                    onClick = {
                        flashMode = when (flashMode) {
                            ImageCapture.FLASH_MODE_AUTO -> ImageCapture.FLASH_MODE_ON
                            ImageCapture.FLASH_MODE_ON -> ImageCapture.FLASH_MODE_OFF
                            else -> ImageCapture.FLASH_MODE_AUTO
                        }
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = when (flashMode) {
                            ImageCapture.FLASH_MODE_ON -> Icons.Default.FlashOn
                            ImageCapture.FLASH_MODE_OFF -> Icons.Default.FlashOff
                            else -> Icons.Default.FlashAuto
                        },
                        contentDescription = "Flash",
                        tint = Color.White
                    )
                }
            } else {
                Spacer(Modifier.size(48.dp))
            }
        }

        Spacer(Modifier.height(12.dp))

        // Camera Viewport or Permission Prompt
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .widthIn(max = 520.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF16161C)),
            contentAlignment = Alignment.Center
        ) {
            if (!hasCameraPermission) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(56.dp)
                    )
                    Text(
                        text = strings.cameraPermissionNeeded,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                        modifier = Modifier
                            .heightIn(min = 48.dp)
                            .testTag("camera_grant_permission_button")
                    ) {
                        Text(strings.takePhoto)
                    }
                    OutlinedButton(
                        onClick = {
                            galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                        modifier = Modifier.heightIn(min = 48.dp)
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(strings.pickPhoto, color = Color.White)
                    }
                }
            } else if (cameraError != null) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = cameraError!!,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFFFB4AB),
                        textAlign = TextAlign.Center
                    )
                    OutlinedButton(
                        onClick = {
                            galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                        modifier = Modifier.heightIn(min = 48.dp)
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(strings.pickPhoto, color = Color.White)
                    }
                }
            } else {
                AndroidView(
                    factory = { previewView },
                    modifier = Modifier.fillMaxSize()
                )
                // Circular Avatar Framing Guide Overlay
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                ) {
                    drawRect(color = Color.Black.copy(alpha = 0.42f))
                    val radius = (min(size.width, size.height) * 0.42f)
                    drawCircle(
                        color = Color.Transparent,
                        radius = radius,
                        center = center,
                        blendMode = BlendMode.Clear
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.85f),
                        radius = radius,
                        center = center,
                        style = Stroke(width = 2.5.dp.toPx())
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Bottom Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 420.dp)
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(
                onClick = {
                    galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                modifier = Modifier
                    .size(52.dp)
                    .background(Color.White.copy(alpha = 0.14f), CircleShape)
            ) {
                Icon(Icons.Default.PhotoLibrary, contentDescription = strings.pickPhoto, tint = Color.White)
            }

            // Shutter Button
            Surface(
                onClick = {
                    val capture = imageCapture ?: return@Surface
                    if (isCapturing || !hasCameraPermission) return@Surface
                    isCapturing = true
                    val outFile = File(context.cacheDir, "avatar_cap_${System.currentTimeMillis()}.jpg")
                    val outputOptions = ImageCapture.OutputFileOptions.Builder(outFile).build()
                    capture.takePicture(
                        outputOptions,
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                isCapturing = false
                                onImageCaptured(outFile)
                            }

                            override fun onError(exception: ImageCaptureException) {
                                isCapturing = false
                                if (outFile.exists()) outFile.delete()
                                cameraError = exception.message ?: "Capture failed."
                            }
                        }
                    )
                },
                enabled = hasCameraPermission && imageCapture != null && !isCapturing,
                shape = CircleShape,
                color = Color.White,
                modifier = Modifier
                    .size(76.dp)
                    .border(4.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .padding(5.dp)
                    .testTag("camera_shutter_button")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (isCapturing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(28.dp),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 3.dp
                        )
                    } else {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = strings.takePhoto,
                            tint = Color(0xFF111827),
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }

            IconButton(
                onClick = { useFrontCamera = !useFrontCamera },
                enabled = hasCameraPermission && canSwitchCamera,
                modifier = Modifier
                    .size(52.dp)
                    .background(Color.White.copy(alpha = 0.14f), CircleShape)
                    .testTag("camera_flip_button")
            ) {
                Icon(
                    Icons.Default.FlipCameraAndroid,
                    contentDescription = strings.flipCamera,
                    tint = if (canSwitchCamera) Color.White else Color.White.copy(alpha = 0.4f)
                )
            }
        }
    }
}

@Composable
private fun ProfileImageCropperContent(
    strings: AppStrings,
    imageUri: Uri,
    canRetake: Boolean,
    onRetake: () -> Unit,
    onCropSaved: (String) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var baseBitmap by remember(imageUri) { mutableStateOf<Bitmap?>(null) }
    var rotationDegrees by remember(imageUri) { mutableIntStateOf(0) }
    var displayBitmap by remember(imageUri) { mutableStateOf<Bitmap?>(null) }
    var loadError by remember(imageUri) { mutableStateOf(false) }

    var zoom by remember(imageUri) { mutableFloatStateOf(1f) }
    var panPxX by remember(imageUri) { mutableFloatStateOf(0f) }
    var panPxY by remember(imageUri) { mutableFloatStateOf(0f) }
    var viewportPx by remember { mutableFloatStateOf(0f) }
    var isSaving by remember { mutableStateOf(false) }

    LaunchedEffect(imageUri) {
        loadError = false
        val decoded = withContext(Dispatchers.IO) {
            ImageCropHelper.decodeOrientedBitmap(context, imageUri)
        }
        if (decoded == null) {
            loadError = true
        } else {
            baseBitmap = decoded
            displayBitmap = decoded
        }
    }

    LaunchedEffect(baseBitmap, rotationDegrees) {
        val src = baseBitmap ?: return@LaunchedEffect
        displayBitmap = withContext(Dispatchers.Default) {
            ImageCropHelper.rotateBitmap(src, rotationDegrees)
        }
        zoom = 1f
        panPxX = 0f
        panPxY = 0f
    }

    fun clampPan(rawX: Float, rawY: Float, targetZoom: Float, bmp: Bitmap, boxSize: Float): Pair<Float, Float> {
        if (boxSize <= 0f) return 0f to 0f
        val srcW = bmp.width.toFloat().coerceAtLeast(1f)
        val srcH = bmp.height.toFloat().coerceAtLeast(1f)
        val baseScale = max(boxSize / srcW, boxSize / srcH)
        val effScale = baseScale * targetZoom
        val maxX = ((srcW * effScale - boxSize) / 2f).coerceAtLeast(0f)
        val maxY = ((srcH * effScale - boxSize) / 2f).coerceAtLeast(0f)
        return rawX.coerceIn(-maxX, maxX) to rawY.coerceIn(-maxY, maxY)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onClose, modifier = Modifier.size(48.dp)) {
                Icon(Icons.Default.Close, contentDescription = strings.backLabel, tint = Color.White)
            }
            Text(
                text = strings.cropPhotoTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            IconButton(
                onClick = { rotationDegrees = (rotationDegrees + 90) % 360 },
                enabled = displayBitmap != null && !isSaving,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("crop_rotate_button")
            ) {
                Icon(Icons.AutoMirrored.Filled.RotateRight, contentDescription = strings.rotatePhoto, tint = Color.White)
            }
        }

        Spacer(Modifier.height(16.dp))

        // Square Interactive Crop Viewport with Circular Mask
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            val bmp = displayBitmap
            when {
                loadError -> {
                    Text(
                        text = "Could not load selected photo.",
                        color = Color(0xFFFFB4AB),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                bmp == null -> {
                    CircularProgressIndicator(color = Color.White)
                }
                else -> {
                    val imageBmp = remember(bmp) { bmp.asImageBitmap() }
                    Box(
                        modifier = Modifier
                            .widthIn(max = 440.dp)
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .clipToBounds()
                            .background(Color.Black)
                            .onSizeChanged { size ->
                                val side = min(size.width, size.height).toFloat()
                                viewportPx = side
                                val clamped = clampPan(panPxX, panPxY, zoom, bmp, side)
                                panPxX = clamped.first
                                panPxY = clamped.second
                            }
                            .pointerInput(bmp, viewportPx) {
                                detectTransformGestures { _, pan, gestureZoom, _ ->
                                    val newZoom = (zoom * gestureZoom).coerceIn(1f, 4f)
                                    val clamped = clampPan(panPxX + pan.x, panPxY + pan.y, newZoom, bmp, viewportPx)
                                    zoom = newZoom
                                    panPxX = clamped.first
                                    panPxY = clamped.second
                                }
                            }
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val srcW = bmp.width.toFloat().coerceAtLeast(1f)
                            val srcH = bmp.height.toFloat().coerceAtLeast(1f)
                            val baseScale = max(size.width / srcW, size.height / srcH)
                            val effScale = baseScale * zoom
                            val drawW = srcW * effScale
                            val drawH = srcH * effScale
                            val drawLeft = (size.width - drawW) / 2f + panPxX
                            val drawTop = (size.height - drawH) / 2f + panPxY

                            drawImage(
                                image = imageBmp,
                                dstOffset = IntOffset(drawLeft.roundToInt(), drawTop.roundToInt()),
                                dstSize = IntSize(drawW.roundToInt().coerceAtLeast(1), drawH.roundToInt().coerceAtLeast(1))
                            )
                        }

                        // Circular Crop Cutout & Guide Ring
                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                        ) {
                            drawRect(color = Color.Black.copy(alpha = 0.58f))
                            val radius = min(size.width, size.height) / 2f
                            drawCircle(
                                color = Color.Transparent,
                                radius = radius,
                                center = center,
                                blendMode = BlendMode.Clear
                            )
                            drawCircle(
                                color = Color.White,
                                radius = radius - 1.dp.toPx(),
                                center = center,
                                style = Stroke(width = 2.dp.toPx())
                            )
                            // Subtle alignment crosshairs
                            val thirdW = size.width / 3f
                            val thirdH = size.height / 3f
                            val gridColor = Color.White.copy(alpha = 0.22f)
                            drawLine(gridColor, Offset(thirdW, 0f), Offset(thirdW, size.height), strokeWidth = 1.dp.toPx())
                            drawLine(gridColor, Offset(thirdW * 2f, 0f), Offset(thirdW * 2f, size.height), strokeWidth = 1.dp.toPx())
                            drawLine(gridColor, Offset(0f, thirdH), Offset(size.width, thirdH), strokeWidth = 1.dp.toPx())
                            drawLine(gridColor, Offset(0f, thirdH * 2f), Offset(size.width, thirdH * 2f), strokeWidth = 1.dp.toPx())
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Accessible Zoom Slider (1x - 4x)
        if (displayBitmap != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 440.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "${strings.zoomLabel}: ${"%.1f".format(zoom)}x",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.85f)
                )
                Slider(
                    value = zoom,
                    onValueChange = { newZoom ->
                        val bmp = displayBitmap
                        zoom = newZoom
                        if (bmp != null) {
                            val clamped = clampPan(panPxX, panPxY, newZoom, bmp, viewportPx)
                            panPxX = clamped.first
                            panPxY = clamped.second
                        }
                    },
                    valueRange = 1f..4f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // Action Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 440.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = if (canRetake) onRetake else onClose,
                enabled = !isSaving,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 50.dp)
                    .testTag("crop_retake_button")
            ) {
                Icon(
                    if (canRetake) Icons.Default.Refresh else Icons.Default.Close,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(if (canRetake) strings.retakePhoto else strings.backLabel, color = Color.White)
            }

            Button(
                onClick = {
                    val bmp = displayBitmap ?: return@Button
                    if (isSaving) return@Button
                    isSaving = true
                    scope.launch {
                        val normX = if (viewportPx > 0f) panPxX / viewportPx else 0f
                        val normY = if (viewportPx > 0f) panPxY / viewportPx else 0f
                        val savedUri = withContext(Dispatchers.IO) {
                            val cropped = ImageCropHelper.cropSquareBitmap(bmp, zoom, normX, normY, 512)
                            ImageCropHelper.saveCroppedAvatar(context, cropped)
                        }
                        isSaving = false
                        if (savedUri != null) {
                            onCropSaved(savedUri)
                        }
                    }
                },
                enabled = displayBitmap != null && !isSaving,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 50.dp)
                    .testTag("crop_confirm_button")
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(strings.usePhoto)
                }
            }
        }
    }
}

private suspend fun getCameraProvider(context: android.content.Context): ProcessCameraProvider =
    suspendCancellableCoroutine { cont ->
        val future = ProcessCameraProvider.getInstance(context)
        future.addListener(
            {
                try {
                    cont.resume(future.get())
                } catch (e: Exception) {
                    cont.resumeWithException(e)
                }
            },
            ContextCompat.getMainExecutor(context)
        )
    }

private fun hasCameraSafe(provider: ProcessCameraProvider, selector: CameraSelector): Boolean {
    return try {
        provider.hasCamera(selector)
    } catch (_: Exception) {
        false
    }
}
