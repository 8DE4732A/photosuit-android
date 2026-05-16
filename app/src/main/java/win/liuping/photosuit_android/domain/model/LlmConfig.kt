package win.liuping.photosuit_android.domain.model

data class LlmConfig(
    val id: Long = 0,
    val name: String = "",
    val apiBase: String = "https://api.openai.com/v1",
    val apiKey: String = "",
    val modelId: String = "gpt-4o",
    val modelType: ModelType = ModelType.MULTIMODAL,
    val isDefault: Boolean = false,
    val maxTokens: Int = 2048,
    val temperature: Float = 0.7f,
    val systemPrompt: String = DEFAULT_SYSTEM_PROMPT,
) {
    companion object {
        const val DEFAULT_SYSTEM_PROMPT = """你是一个专业的摄影后期处理助手。用户会描述他们想要的照片水印边框效果，你需要理解他们的需求并生成对应的参数配置。

请以JSON格式返回配置，包含以下字段：
- showCameraModel: boolean
- showLens: boolean
- showFocalLength: boolean
- showAperture: boolean
- showShutterSpeed: boolean
- showIso: boolean
- showDateTime: boolean
- showCameraLogo: boolean
- borderStyle: "WHITE_BOTTOM" | "BLACK_BOTTOM" | "FILM_STRIP" | "MINIMAL" | "FULL_FRAME" | "NONE"
- borderColor: 颜色十六进制字符串如"#FFFFFF"
- textColor: 颜色十六进制字符串
- accentColor: 颜色十六进制字符串
- fontScale: 0.5-2.0
- cornerRadius: 0-50
- paddingScale: 0.5-2.0
- logoPosition: "LEFT" | "RIGHT"
- dividerVisible: boolean"""
    }
}

enum class ModelType(val displayName: String) {
    TEXT_ONLY("纯文本模型"),
    MULTIMODAL("多模态模型"),
}
