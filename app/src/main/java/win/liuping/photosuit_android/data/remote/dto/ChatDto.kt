package win.liuping.photosuit_android.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ChatRequest(
    val model: String,
    val messages: List<ChatMessage>,
    @SerializedName("max_tokens") val maxTokens: Int = 2048,
    val temperature: Float = 0.7f,
    val stream: Boolean = false,
)

data class ChatMessage(
    val role: String,
    val content: Any, // String or List<ContentPart>
)

data class ContentPart(
    val type: String,
    val text: String? = null,
    @SerializedName("image_url") val imageUrl: ImageUrl? = null,
)

data class ImageUrl(
    val url: String,
    val detail: String = "low",
)

data class ChatResponse(
    val id: String?,
    val choices: List<Choice>,
    val usage: Usage?,
)

data class Choice(
    val index: Int,
    val message: ChatMessage?,
    @SerializedName("finish_reason") val finishReason: String?,
)

data class Usage(
    @SerializedName("prompt_tokens") val promptTokens: Int,
    @SerializedName("completion_tokens") val completionTokens: Int,
    @SerializedName("total_tokens") val totalTokens: Int,
)
