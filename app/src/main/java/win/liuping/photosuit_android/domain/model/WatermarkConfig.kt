package win.liuping.photosuit_android.domain.model

import androidx.compose.ui.graphics.Color

data class WatermarkConfig(
    val id: Long = 0,
    val name: String = "默认样式",
    val showCameraModel: Boolean = true,
    val showLens: Boolean = true,
    val showFocalLength: Boolean = true,
    val showAperture: Boolean = true,
    val showShutterSpeed: Boolean = true,
    val showIso: Boolean = true,
    val showDateTime: Boolean = true,
    val showGps: Boolean = false,
    val showCameraLogo: Boolean = true,
    val borderStyle: BorderStyle = BorderStyle.WHITE_BOTTOM,
    val borderColor: Long = 0xFFFFFFFF,
    val textColor: Long = 0xFF333333,
    val accentColor: Long = 0xFF000000,
    val fontScale: Float = 1.0f,
    val cornerRadius: Float = 0f,
    val paddingScale: Float = 1.0f,
    val logoPosition: LogoPosition = LogoPosition.LEFT,
    val dividerVisible: Boolean = true,
) {
    val borderColorValue get() = Color(borderColor)
    val textColorValue get() = Color(textColor)
    val accentColorValue get() = Color(accentColor)
}

enum class BorderStyle(val displayName: String) {
    WHITE_BOTTOM("白色底部"),
    BLACK_BOTTOM("黑色底部"),
    FILM_STRIP("胶片风格"),
    MINIMAL("极简边框"),
    FULL_FRAME("全边框"),
    NONE("无边框"),
}

enum class LogoPosition(val displayName: String) {
    LEFT("左侧"),
    RIGHT("右侧"),
}
