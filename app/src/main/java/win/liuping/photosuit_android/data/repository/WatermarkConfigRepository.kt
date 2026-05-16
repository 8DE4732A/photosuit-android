package win.liuping.photosuit_android.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import win.liuping.photosuit_android.data.local.dao.WatermarkConfigDao
import win.liuping.photosuit_android.data.local.entity.WatermarkConfigEntity
import win.liuping.photosuit_android.domain.model.BorderStyle
import win.liuping.photosuit_android.domain.model.LogoPosition
import win.liuping.photosuit_android.domain.model.WatermarkConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WatermarkConfigRepository @Inject constructor(
    private val dao: WatermarkConfigDao,
) {
    fun getAll(): Flow<List<WatermarkConfig>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    suspend fun getById(id: Long): WatermarkConfig? = dao.getById(id)?.toDomain()

    suspend fun save(config: WatermarkConfig): Long = dao.insert(config.toEntity())

    suspend fun update(config: WatermarkConfig) = dao.update(config.toEntity())

    suspend fun delete(id: Long) = dao.deleteById(id)
}

private fun WatermarkConfigEntity.toDomain() = WatermarkConfig(
    id = id,
    name = name,
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
)

private fun WatermarkConfig.toEntity() = WatermarkConfigEntity(
    id = id,
    name = name,
    showCameraModel = showCameraModel,
    showLens = showLens,
    showFocalLength = showFocalLength,
    showAperture = showAperture,
    showShutterSpeed = showShutterSpeed,
    showIso = showIso,
    showDateTime = showDateTime,
    showGps = showGps,
    showCameraLogo = showCameraLogo,
    borderStyle = borderStyle.name,
    borderColor = borderColor,
    textColor = textColor,
    accentColor = accentColor,
    fontScale = fontScale,
    cornerRadius = cornerRadius,
    paddingScale = paddingScale,
    logoPosition = logoPosition.name,
    dividerVisible = dividerVisible,
)
