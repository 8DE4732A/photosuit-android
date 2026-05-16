package win.liuping.photosuit_android.ui.screen.llm

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import win.liuping.photosuit_android.domain.model.LlmConfig

@Composable
fun LlmConfigListScreen(
    onBack: () -> Unit,
    onAddConfig: () -> Unit,
    onEditConfig: (Long) -> Unit,
    viewModel: LlmConfigListViewModel = hiltViewModel(),
) {
    val configs by viewModel.configs.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("LLM 配置") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary, titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        },
        floatingActionButton = { FloatingActionButton(onClick = onAddConfig) { Icon(Icons.Default.Add, null) } }
    ) { padding ->
        if (configs.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("暂无LLM配置", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = onAddConfig) { Text("添加配置") }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(configs) { config ->
                    LlmConfigCard(
                        config = config,
                        onClick = { onEditConfig(config.id) },
                        onSetDefault = { viewModel.setDefault(config.id) },
                        onDelete = { viewModel.delete(config) },
                    )
                }
            }
        }
    }
}

@Composable
private fun LlmConfigCard(config: LlmConfig, onClick: () -> Unit, onSetDefault: () -> Unit, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), shape = RoundedCornerShape(16.dp)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(config.name, fontWeight = FontWeight.Bold)
                    if (config.isDefault) {
                        Spacer(Modifier.width(6.dp))
                        Icon(Icons.Default.CheckCircle, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    }
                }
                Text(config.modelId)
                Text(config.modelType.displayName, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
            if (!config.isDefault) TextButton(onClick = onSetDefault) { Text("设默认") }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null) }
        }
    }
}
