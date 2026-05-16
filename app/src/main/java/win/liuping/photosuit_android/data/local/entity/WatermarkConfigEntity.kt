package win.liuping.photosuit_android.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watermark_configs")
data class WatermarkConfigEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
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
