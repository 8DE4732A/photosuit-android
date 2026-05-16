package win.liuping.photosuit_android.ui.screen.llm

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import win.liuping.photosuit_android.domain.model.ModelType

@Composable
fun LlmConfigEditScreen(
    configId: Long,
    onBack: () -> Unit,
    viewModel: LlmConfigEditViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val config = state.config

    LaunchedEffect(configId) { viewModel.load(configId) }
    LaunchedEffect(state.isSaved) { if (state.isSaved) onBack() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (configId == 0L) "添加LLM配置" else "编辑LLM配置") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } },
                actions = { IconButton(onClick = viewModel::save, enabled = config.name.isNotBlank() && config.apiBase.isNotBlank() && config.modelId.isNotBlank()) { Icon(Icons.Default.Save, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary, titleContentColor = Color.White, navigationIconContentColor = Color.White, actionIconContentColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            OutlinedTextField(config.name, { viewModel.update(config.copy(name = it)) }, label = { Text("名称") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(config.apiBase, { viewModel.update(config.copy(apiBase = it)) }, label = { Text("apiBase") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(config.apiKey, { viewModel.update(config.copy(apiKey = it)) }, label = { Text("apiKey") }, modifier = Modifier.fillMaxWidth(), singleLine = true, visualTransformation = PasswordVisualTransformation())
            OutlinedTextField(config.modelId, { viewModel.update(config.copy(modelId = it)) }, label = { Text("modelId") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

            Text("模型类型")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ModelType.entries.forEach { type ->
                    FilterChip(
                        selected = config.modelType == type,
                        onClick = { viewModel.update(config.copy(modelType = type)) },
                        label = { Text(type.displayName) },
                    )
                }
            }

            Text("Temperature: ${String.format("%.1f", config.temperature)}")
            Slider(config.temperature, { viewModel.update(config.copy(temperature = it)) }, valueRange = 0f..2f)

            OutlinedTextField(config.maxTokens.toString(), { value ->
                value.toIntOrNull()?.let { viewModel.update(config.copy(maxTokens = it)) }
            }, label = { Text("maxTokens") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Checkbox(config.isDefault, { viewModel.update(config.copy(isDefault = it)) })
                Text("设为默认配置")
            }

            OutlinedTextField(
                config.systemPrompt,
                { viewModel.update(config.copy(systemPrompt = it)) },
                label = { Text("系统提示词") },
                modifier = Modifier.fillMaxWidth().height(220.dp),
                minLines = 8,
            )
        }
    }
}
