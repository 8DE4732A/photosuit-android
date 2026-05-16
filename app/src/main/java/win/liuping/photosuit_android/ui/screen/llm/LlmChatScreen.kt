package win.liuping.photosuit_android.ui.screen.llm

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun LlmChatScreen(
    onBack: () -> Unit,
    onOpenLlmConfigs: () -> Unit,
    viewModel: LlmChatViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI 风格助手") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } },
                actions = { IconButton(onClick = onOpenLlmConfigs) { Icon(Icons.Default.Settings, null) } },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White,
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = state.input,
                    onValueChange = viewModel::updateInput,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("描述你想要的水印效果，例如：徕卡红标、黑色电影感边框...") },
                    minLines = 1,
                    maxLines = 4,
                )
                FilledIconButton(onClick = viewModel::send, enabled = !state.isLoading && state.input.isNotBlank()) {
                    if (state.isLoading) CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = Color.White)
                    else Icon(Icons.Default.Send, null)
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
        ) {
            item {
                AssistCard(configName = state.currentConfig?.name)
            }
            items(state.messages) { msg ->
                MessageBubble(role = msg.role, content = msg.content)
            }
            if (state.generatedConfig != null) {
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                        Column(Modifier.padding(16.dp)) {
                            Text("已生成可用的水印参数", fontWeight = FontWeight.Bold)
                            Text("后续可在编辑器中接入一键应用/保存为预设。")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AssistCard(configName: String?) {
    Card(shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("用自然语言生成照片水印样式", fontWeight = FontWeight.Bold)
            Text("当前模型：${configName ?: "未配置"}")
            Text("支持 OpenAI 兼容 API，可配置 apiBase、apiKey、modelId，并标记纯文本或多模态模型。")
        }
    }
}

@Composable
private fun MessageBubble(role: String, content: String) {
    val isUser = role == "user"
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 320.dp)
                .background(
                    if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    RoundedCornerShape(16.dp),
                )
                .padding(12.dp),
        ) {
            Text(content, color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
