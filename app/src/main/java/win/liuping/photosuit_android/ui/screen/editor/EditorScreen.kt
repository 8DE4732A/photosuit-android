package win.liuping.photosuit_android.ui.screen.editor

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import win.liuping.photosuit_android.domain.model.BorderStyle
import win.liuping.photosuit_android.domain.model.StylePreset
import win.liuping.photosuit_android.domain.model.WatermarkConfig

@Composable
fun EditorScreen(
    photoUri: Uri?,
    onBack: () -> Unit,
    onOpenPresets: () -> Unit,
    onOpenLlmChat: () -> Unit,
    viewModel: EditorViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val presets by viewModel.savedPresets.collectAsStateWithLifecycle()
    var showSavePresetDialog by remember { mutableStateOf(false) }
    var showSaveSuccessSnackbar by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(photoUri) {
        photoUri?.let { viewModel.loadPhoto(it) }
    }

    LaunchedEffect(uiState.savedPath) {
        if (uiState.savedPath != null) {
            snackbarHostState.showSnackbar("已保存到: ${uiState.savedPath}")
            viewModel.clearSavedPath()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar("错误: $it")
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("编辑照片") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = onOpenLlmChat) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "AI助手")
                    }
                    IconButton(onClick = { showSavePresetDialog = true }) {
                        Icon(Icons.Default.BookmarkAdd, contentDescription = "保存样式")
                    }
                    IconButton(
                        onClick = { viewModel.savePhoto() },
                        enabled = uiState.previewBitmap != null && !uiState.isSaving,
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Save, contentDescription = "保存")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White,
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            // Preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(Color(0xFF2A2A2A)),
                contentAlignment = Alignment.Center,
            ) {
                when {
                    uiState.isLoading -> CircularProgressIndicator(color = Color.White)
                    uiState.previewBitmap != null -> Image(
                        bitmap = uiState.previewBitmap!!.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit,
                    )
                    else -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Image, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                        Text("预览区域", color = Color.Gray)
                    }
                }
            }

            // Controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Presets row
                Text("快速样式", fontWeight = FontWeight.SemiBold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(presets) { preset ->
                        PresetChip(preset = preset, onClick = { viewModel.applyPreset(preset) })
                    }
                }

                HorizontalDivider()

                // Border style
                Text("边框样式", fontWeight = FontWeight.SemiBold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(BorderStyle.entries) { style ->
                        FilterChip(
                            selected = uiState.config.borderStyle == style,
                            onClick = { viewModel.updateConfig(uiState.config.copy(borderStyle = style)) },
                            label = { Text(style.displayName, fontSize = 12.sp) },
                        )
                    }
                }

                // Show/hide fields
                Text("显示字段", fontWeight = FontWeight.SemiBold)
                ExifFieldToggles(config = uiState.config, onConfigChange = viewModel::updateConfig)

                // Font scale
                Text("文字大小: ${(uiState.config.fontScale * 100).toInt()}%", fontWeight = FontWeight.SemiBold)
                Slider(
                    value = uiState.config.fontScale,
                    onValueChange = { viewModel.updateConfig(uiState.config.copy(fontScale = it)) },
                    valueRange = 0.5f..2.0f,
                )

                // Padding scale
                Text("内边距: ${(uiState.config.paddingScale * 100).toInt()}%", fontWeight = FontWeight.SemiBold)
                Slider(
                    value = uiState.config.paddingScale,
                    onValueChange = { viewModel.updateConfig(uiState.config.copy(paddingScale = it)) },
                    valueRange = 0.5f..2.0f,
                )
            }
        }
    }

    if (showSavePresetDialog) {
        SavePresetDialog(
            onConfirm = { name, desc ->
                viewModel.savePreset(name, desc)
                showSavePresetDialog = false
            },
            onDismiss = { showSavePresetDialog = false },
        )
    }
}

@Composable
private fun PresetChip(preset: StylePreset, onClick: () -> Unit) {
    Card(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
            Text(preset.name, fontWeight = FontWeight.Medium, fontSize = 13.sp)
            if (preset.description.isNotBlank()) {
                Text(preset.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
        }
    }
}

@Composable
private fun ExifFieldToggles(config: WatermarkConfig, onConfigChange: (WatermarkConfig) -> Unit) {
    val fields = listOf(
        "相机型号" to config.showCameraModel to { v: Boolean -> config.copy(showCameraModel = v) },
        "镜头信息" to config.showLens to { v: Boolean -> config.copy(showLens = v) },
        "焦距" to config.showFocalLength to { v: Boolean -> config.copy(showFocalLength = v) },
        "光圈" to config.showAperture to { v: Boolean -> config.copy(showAperture = v) },
        "快门速度" to config.showShutterSpeed to { v: Boolean -> config.copy(showShutterSpeed = v) },
        "ISO" to config.showIso to { v: Boolean -> config.copy(showIso = v) },
        "拍摄时间" to config.showDateTime to { v: Boolean -> config.copy(showDateTime = v) },
        "相机Logo" to config.showCameraLogo to { v: Boolean -> config.copy(showCameraLogo = v) },
    )

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        fields.chunked(2).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { (labelValue, updater) ->
                    val (label, value) = labelValue
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Checkbox(
                            checked = value,
                            onCheckedChange = { onConfigChange(updater(it)) },
                        )
                        Text(label, fontSize = 13.sp)
                    }
                }
                if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun SavePresetDialog(onConfirm: (String, String) -> Unit, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("保存样式预设") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("预设名称") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("描述（可选）") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { if (name.isNotBlank()) onConfirm(name, desc) }, enabled = name.isNotBlank()) {
                Text("保存")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } },
    )
}
