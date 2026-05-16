package win.liuping.photosuit_android.ui.screen.home

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

@Composable
fun HomeScreen(
    onPickPhoto: (Uri) -> Unit,
    onOpenPresets: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenLlmChat: () -> Unit,
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let { onPickPhoto(it) } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PhotoSuit", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "设置")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White,
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.tertiary,
                            )
                        )
                    )
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.AddAPhoto,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(56.dp),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("选择照片", color = Color.White, fontWeight = FontWeight.Medium)
                }
            }

            Text(
                text = "点击上方区域选择照片，添加水印边框",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                QuickActionCard(
                    icon = Icons.Default.Style,
                    title = "样式预设",
                    subtitle = "管理水印样式",
                    modifier = Modifier.weight(1f),
                    onClick = onOpenPresets,
                )
                QuickActionCard(
                    icon = Icons.Default.AutoAwesome,
                    title = "AI 助手",
                    subtitle = "对话生成效果",
                    modifier = Modifier.weight(1f),
                    onClick = onOpenLlmChat,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                QuickActionCard(
                    icon = Icons.Default.PhotoLibrary,
                    title = "相册选图",
                    subtitle = "从相册选择",
                    modifier = Modifier.weight(1f),
                    onClick = { launcher.launch("image/*") },
                )
                QuickActionCard(
                    icon = Icons.Default.Tune,
                    title = "LLM 配置",
                    subtitle = "配置AI模型",
                    modifier = Modifier.weight(1f),
                    onClick = onOpenSettings,
                )
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        }
    }
}
