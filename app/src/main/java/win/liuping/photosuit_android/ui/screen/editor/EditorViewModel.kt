package win.liuping.photosuit_android.ui.screen.editor

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import win.liuping.photosuit_android.data.repository.StylePresetRepository
import win.liuping.photosuit_android.data.repository.WatermarkConfigRepository
import win.liuping.photosuit_android.domain.model.BuiltInPresets
import win.liuping.photosuit_android.domain.model.ExifData
import win.liuping.photosuit_android.domain.model.StylePreset
import win.liuping.photosuit_android.domain.model.WatermarkConfig
import win.liuping.photosuit_android.util.ExifReader
import win.liuping.photosuit_android.util.WatermarkRenderer
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

data class EditorUiState(
    val sourceBitmap: Bitmap? = null,
    val previewBitmap: Bitmap? = null,
    val exifData: ExifData = ExifData(),
    val config: WatermarkConfig = WatermarkConfig(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val savedPath: String? = null,
    val error: String? = null,
)

@HiltViewModel
class EditorViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val exifReader: ExifReader,
    private val renderer: WatermarkRenderer,
    private val presetRepo: StylePresetRepository,
    private val configRepo: WatermarkConfigRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

    val savedPresets: StateFlow<List<StylePreset>> = presetRepo.getAll()
        .map { it + BuiltInPresets.list }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BuiltInPresets.list)

    fun loadPhoto(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching {
                val exif = withContext(Dispatchers.IO) { exifReader.read(context, uri) }
                val bitmap = loadBitmap(uri)
                _uiState.update { it.copy(exifData = exif, sourceBitmap = bitmap, isLoading = false) }
                renderPreview()
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun updateConfig(config: WatermarkConfig) {
        _uiState.update { it.copy(config = config) }
        renderPreview()
    }

    fun applyPreset(preset: StylePreset) {
        _uiState.update { it.copy(config = preset.watermarkConfig) }
        renderPreview()
    }

    fun savePreset(name: String, description: String) {
        viewModelScope.launch {
            val config = _uiState.value.config
            presetRepo.save(
                StylePreset(name = name, description = description, watermarkConfig = config)
            )
        }
    }

    fun savePhoto() {
        viewModelScope.launch {
            val bitmap = _uiState.value.previewBitmap ?: return@launch
            _uiState.update { it.copy(isSaving = true) }
            runCatching {
                val path = withContext(Dispatchers.IO) {
                    val dir = File(context.getExternalFilesDir(null), "PhotoSuit")
                    dir.mkdirs()
                    val file = File(dir, "photosuit_${System.currentTimeMillis()}.jpg")
                    FileOutputStream(file).use { out ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
                    }
                    file.absolutePath
                }
                _uiState.update { it.copy(isSaving = false, savedPath = path) }
            }.onFailure { e ->
                _uiState.update { it.copy(isSaving = false, error = e.message) }
            }
        }
    }

    fun clearSavedPath() = _uiState.update { it.copy(savedPath = null) }
    fun clearError() = _uiState.update { it.copy(error = null) }

    private fun renderPreview() {
        viewModelScope.launch {
            val state = _uiState.value
            val source = state.sourceBitmap ?: return@launch
            val exif = state.exifData
            val config = state.config
            val logo = exif.cameraLogoKey?.let { loadLogo(it) }
            val preview = withContext(Dispatchers.Default) {
                renderer.render(source, config, exif, logo)
            }
            _uiState.update { it.copy(previewBitmap = preview) }
        }
    }

    private suspend fun loadBitmap(uri: Uri): Bitmap {
        val loader = ImageLoader(context)
        val request = ImageRequest.Builder(context).data(uri).allowHardware(false).build()
        val result = loader.execute(request)
        return (result as SuccessResult).drawable.let {
            (it as android.graphics.drawable.BitmapDrawable).bitmap
        }
    }

    private suspend fun loadLogo(logoKey: String): Bitmap? = withContext(Dispatchers.IO) {
        runCatching {
            val loader = ImageLoader.Builder(context).components {
                add(coil.decode.SvgDecoder.Factory())
            }.build()
            val assetPath = "logos/${logoKey}.svg"
            val uri = Uri.parse("file:///android_asset/$assetPath")
            val request = ImageRequest.Builder(context).data(uri).size(128, 128).allowHardware(false).build()
            val result = loader.execute(request)
            (result as? SuccessResult)?.drawable?.let {
                (it as? android.graphics.drawable.BitmapDrawable)?.bitmap
            }
        }.getOrNull()
    }
}
