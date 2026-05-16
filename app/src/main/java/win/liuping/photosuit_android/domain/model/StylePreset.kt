package win.liuping.photosuit_android.domain.model

data class StylePreset(
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val watermarkConfig: WatermarkConfig,
    val createdAt: Long = System.currentTimeMillis(),
    val isBuiltIn: Boolean = false,
)

object BuiltInPresets {
    val list = listOf(
        StylePreset(
            id = -1,
            name = "经典白边",
            description = "白色底部边框，显示完整拍摄参数",
            watermarkConfig = WatermarkConfig(
                borderStyle = BorderStyle.WHITE_BOTTOM,
                borderColor = 0xFFFFFFFF,
                textColor = 0xFF222222,
                accentColor = 0xFF000000,
                showCameraLogo = true,
                dividerVisible = true,
            ),
            isBuiltIn = true,
        ),
        StylePreset(
            id = -2,
            name = "暗黑影调",
            description = "黑色底部边框，金色文字",
            watermarkConfig = WatermarkConfig(
                borderStyle = BorderStyle.BLACK_BOTTOM,
                borderColor = 0xFF1A1A1A,
                textColor = 0xFFD4AF37,
                accentColor = 0xFFFFFFFF,
                showCameraLogo = true,
                dividerVisible = true,
            ),
            isBuiltIn = true,
        ),
        StylePreset(
            id = -3,
            name = "胶片复古",
            description = "模拟胶片边框风格",
            watermarkConfig = WatermarkConfig(
                borderStyle = BorderStyle.FILM_STRIP,
                borderColor = 0xFF2B2B2B,
                textColor = 0xFFE8DCC8,
                accentColor = 0xFFC8A96E,
                showCameraLogo = true,
                fontScale = 0.9f,
            ),
            isBuiltIn = true,
        ),
        StylePreset(
            id = -4,
            name = "极简主义",
            description = "最小化信息展示",
            watermarkConfig = WatermarkConfig(
                borderStyle = BorderStyle.MINIMAL,
                borderColor = 0xFFFFFFFF,
                textColor = 0xFF666666,
                accentColor = 0xFF333333,
                showCameraLogo = false,
                showDateTime = false,
                showGps = false,
                dividerVisible = false,
                fontScale = 0.85f,
            ),
            isBuiltIn = true,
        ),
    )
}
