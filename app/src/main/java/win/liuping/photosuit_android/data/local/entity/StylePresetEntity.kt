package win.liuping.photosuit_android.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "style_presets")
data class StylePresetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val createdAt: Long,
    val isBuiltIn: Boolean,
    // WatermarkConfig fields embedded
    val showCameraModel: Boolean,
    val showLens: Boolean,
    val showFocalLength: Boolean,
    val showAperture: Boolean,
    val showShutterSpeed: Boolean,
    val showIso: Boolean,
    val showDateTime: Boolean,
    val showGps: Boolean,
    val showCameraLogo: Boolean,
    val borderStyle: String,
    val borderColor: Long,
    val textColor: Long,
    val accentColor: Long,
    val fontScale: Float,
    val cornerRadius: Float,
    val paddingScale: Float,
    val logoPosition: String,
    val dividerVisible: Boolean,
)
