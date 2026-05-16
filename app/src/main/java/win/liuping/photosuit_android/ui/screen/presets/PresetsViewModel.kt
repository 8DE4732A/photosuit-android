package win.liuping.photosuit_android.ui.screen.presets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import win.liuping.photosuit_android.data.repository.StylePresetRepository
import win.liuping.photosuit_android.domain.model.BuiltInPresets
import javax.inject.Inject

@HiltViewModel
class PresetsViewModel @Inject constructor(
    private val repo: StylePresetRepository,
) : ViewModel() {
    val presets = repo.getAll()
        .map { saved -> BuiltInPresets.list + saved }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BuiltInPresets.list)

    fun delete(id: Long) {
        if (id > 0) viewModelScope.launch { repo.delete(id) }
    }
}
