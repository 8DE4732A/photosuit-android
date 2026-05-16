package win.liuping.photosuit_android.ui.screen.presets

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import win.liuping.photosuit_android.domain.model.StylePreset

@Composable
fun PresetsScreen(
    onBack: () -> Unit,
    viewModel: PresetsViewModel = hiltViewModel(),
) {
    val presets by viewModel.presets.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("样式预设") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary, titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(presets) { preset ->
                PresetCard(preset = preset, onDelete = { viewModel.delete(preset.id) })
            }
        }
    }
}

@Composable
private fun PresetCard(preset: StylePreset, onDelete: () -> Unit) {
    Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp)) {
            Column(Modifier.weight(1f)) {
                Text(preset.name, fontWeight = FontWeight.Bold)
                if (preset.description.isNotBlank()) Text(preset.description, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                Spacer(Modifier.height(6.dp))
                Text("${preset.watermarkConfig.borderStyle.displayName} · ${if (preset.watermarkConfig.showCameraLogo) "显示Logo" else "隐藏Logo"}", style = MaterialTheme.typography.bodySmall)
            }
            if (!preset.isBuiltIn) IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null) }
        }
    }
}
