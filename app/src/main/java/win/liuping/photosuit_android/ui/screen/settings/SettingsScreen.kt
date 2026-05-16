package win.liuping.photosuit_android.ui.screen.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onOpenLlmConfigs: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary, titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SettingItem(
                icon = Icons.Default.Psychology,
                title = "LLM 模型配置",
                subtitle = "配置 apiBase、apiKey、modelId，支持纯文本和多模态模型",
                onClick = onOpenLlmConfigs,
            )
            SettingItem(
                icon = Icons.Default.Tune,
                title = "水印渲染",
                subtitle = "支持白边、黑边、胶片、极简和全边框样式",
                onClick = {},
            )
            SettingItem(
                icon = Icons.Default.Storage,
                title = "相机Logo资源",
                subtitle = "已内置 46 个品牌SVG Logo，来自项目assets/logos",
                onClick = {},
            )
            SettingItem(
                icon = Icons.Default.Code,
                title = "技术栈",
                subtitle = "Jetpack Compose、Room、Hilt、Retrofit、Coil、EXIFInterface",
                onClick = {},
            )
        }
    }
}

@Composable
private fun SettingItem(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), shape = RoundedCornerShape(16.dp)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(16.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold)
                Text(subtitle, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f))
            }
        }
    }
}
