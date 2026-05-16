package win.liuping.photosuit_android.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import win.liuping.photosuit_android.data.local.dao.StylePresetDao
import win.liuping.photosuit_android.data.local.entity.StylePresetEntity
import win.liuping.photosuit_android.domain.model.BorderStyle
import win.liuping.photosuit_android.domain.model.LogoPosition
import win.liuping.photosuit_android.domain.model.StylePreset
import win.liuping.photosuit_android.domain.model.WatermarkConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StylePresetRepository @Inject constructor(
    private val dao: StylePresetDao,
) {
    fun getAll(): Flow<List<StylePreset>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    suspend fun getById(id: Long): StylePreset? = dao.getById(id)?.toDomain()

    suspend fun save(preset: StylePreset): Long = dao.insert(preset.toEntity())

    suspend fun delete(id: Long) = dao.deleteById(id)
}

private fun StylePresetEntity.toDomain() = StylePreset(
    id = id,
    name = name,
    description = description,
    createdAt = createdAt,
    isBuiltIn = isBuiltIn,
    watermarkConfig = WatermarkConfig(
        showCameraModel = showCameraModel,
        showLens = showLens,
        showFocalLength = showFocalLength,
        showAperture = showAperture,
        showShutterSpeed = showShutterSpeed,
        showIso = showIso,
        showDateTime = showDateTime,
        showGps = showGps,
        showCameraLogo = showCameraLogo,
        borderStyle = BorderStyle.valueOf(borderStyle),
        borderColor = borderColor,
        textColor = textColor,
        accentColor = accentColor,
        fontScale = fontScale,
        cornerRadius = cornerRadius,
        paddingScale = paddingScale,
        logoPosition = LogoPosition.valueOf(logoPosition),
        dividerVisible = dividerVisible,
    ),
)

private fun StylePreset.toEntity() = StylePresetEntity(
    id = id,
    name = name,
    description = description,
    createdAt = createdAt,
    isBuiltIn = isBuiltIn,
    showCameraModel = watermarkConfig.showCameraModel,
    showLens = watermarkConfig.showLens,
    showFocalLength = watermarkConfig.showFocalLength,
    showAperture = watermarkConfig.showAperture,
    showShutterSpeed = watermarkConfig.showShutterSpeed,
    showIso = watermarkConfig.showIso,
    showDateTime = watermarkConfig.showDateTime,
    showGps = watermarkConfig.showGps,
    showCameraLogo = watermarkConfig.showCameraLogo,
    borderStyle = watermarkConfig.borderStyle.name,
    borderColor = watermarkConfig.borderColor,
    textColor = watermarkConfig.textColor,
    accentColor = watermarkConfig.accentColor,
    fontScale = watermarkConfig.fontScale,
    cornerRadius = watermarkConfig.cornerRadius,
    paddingScale = watermarkConfig.paddingScale,
    logoPosition = watermarkConfig.logoPosition.name,
    dividerVisible = watermarkConfig.dividerVisible,
)
