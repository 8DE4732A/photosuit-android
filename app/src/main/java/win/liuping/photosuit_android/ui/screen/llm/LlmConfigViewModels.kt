package win.liuping.photosuit_android.ui.screen.llm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import win.liuping.photosuit_android.data.repository.ChatHistoryItem
import win.liuping.photosuit_android.data.repository.LlmConfigRepository
import win.liuping.photosuit_android.data.repository.LlmRepository
import win.liuping.photosuit_android.domain.model.LlmConfig
import win.liuping.photosuit_android.domain.model.ModelType
import win.liuping.photosuit_android.domain.model.WatermarkConfig
import javax.inject.Inject

data class LlmChatUiState(
    val input: String = "",
    val messages: List<ChatHistoryItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentConfig: LlmConfig? = null,
    val generatedConfig: WatermarkConfig? = null,
)

@HiltViewModel
class LlmChatViewModel @Inject constructor(
    private val configRepo: LlmConfigRepository,
    private val llmRepo: LlmRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(LlmChatUiState())
    val uiState: StateFlow<LlmChatUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update { it.copy(currentConfig = configRepo.getDefault()) }
        }
    }

    fun updateInput(value: String) = _uiState.update { it.copy(input = value) }

    fun send() {
        val state = _uiState.value
        val config = state.currentConfig ?: run {
            _uiState.update { it.copy(error = "请先配置默认LLM模型") }
            return
        }
        if (state.input.isBlank()) return

        viewModelScope.launch {
            val userText = state.input.trim()
            val newMessages = state.messages + ChatHistoryItem("user", userText)
            _uiState.update { it.copy(input = "", messages = newMessages, isLoading = true, error = null) }

            llmRepo.chat(config, userText, state.messages).fold(
                onSuccess = { reply ->
                    val generated = llmRepo.parseWatermarkConfig(reply, WatermarkConfig())
                    _uiState.update {
                        it.copy(
                            messages = it.messages + ChatHistoryItem("assistant", reply),
                            isLoading = false,
                            generatedConfig = generated,
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message ?: "请求失败") }
                }
            )
        }
    }

    fun clearError() = _uiState.update { it.copy(error = null) }
}

@HiltViewModel
class LlmConfigListViewModel @Inject constructor(
    private val repo: LlmConfigRepository,
) : ViewModel() {
    val configs = repo.getAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setDefault(id: Long) = viewModelScope.launch { repo.setDefault(id) }
    fun delete(config: LlmConfig) = viewModelScope.launch { repo.delete(config) }
}

data class LlmConfigEditUiState(
    val config: LlmConfig = LlmConfig(name = "OpenAI兼容模型"),
    val isSaved: Boolean = false,
)

@HiltViewModel
class LlmConfigEditViewModel @Inject constructor(
    private val repo: LlmConfigRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(LlmConfigEditUiState())
    val uiState: StateFlow<LlmConfigEditUiState> = _uiState.asStateFlow()

    fun load(id: Long) {
        if (id == 0L) return
        viewModelScope.launch {
            repo.getById(id)?.let { config -> _uiState.update { it.copy(config = config) } }
        }
    }

    fun update(config: LlmConfig) = _uiState.update { it.copy(config = config) }

    fun save() {
        viewModelScope.launch {
            val config = _uiState.value.config
            val id = repo.save(config)
            if (config.isDefault) repo.setDefault(if (config.id == 0L) id else config.id)
            _uiState.update { it.copy(isSaved = true) }
        }
    }
}
