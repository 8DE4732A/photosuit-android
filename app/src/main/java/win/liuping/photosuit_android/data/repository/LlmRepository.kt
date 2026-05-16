package win.liuping.photosuit_android.data.repository

import android.graphics.Bitmap
import android.util.Base64
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import win.liuping.photosuit_android.data.remote.api.LlmApi
import win.liuping.photosuit_android.data.remote.dto.ChatMessage
import win.liuping.photosuit_android.data.remote.dto.ChatRequest
import win.liuping.photosuit_android.data.remote.dto.ContentPart
import win.liuping.photosuit_android.data.remote.dto.ImageUrl
import win.liuping.photosuit_android.domain.model.LlmConfig
import win.liuping.photosuit_android.domain.model.ModelType
import win.liuping.photosuit_android.domain.model.WatermarkConfig
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

data class ChatHistoryItem(
    val role: String,
    val content: String,
)

@Singleton
class LlmRepository @Inject constructor(
    private val gson: Gson,
) {
    private var currentConfig: LlmConfig? = null
    private var currentApi: LlmApi? = null

    fun updateConfig(config: LlmConfig) {
        if (currentConfig?.apiBase != config.apiBase || currentConfig?.apiKey != config.apiKey) {
            currentApi = buildApi(config)
        }
        currentConfig = config
    }

    private fun buildApi(config: LlmConfig): LlmApi {
        val baseUrl = config.apiBase.trimEnd('/') + "/"
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${config.apiKey}")
                    .addHeader("Content-Type", "application/json")
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LlmApi::class.java)
    }

    suspend fun chat(
        config: LlmConfig,
        userMessage: String,
        history: List<ChatHistoryItem>,
        bitmap: Bitmap? = null,
    ): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            updateConfig(config)
            val api = currentApi ?: throw IllegalStateException("API未初始化")

            val messages = mutableListOf<ChatMessage>()
            if (config.systemPrompt.isNotBlank()) {
                messages.add(ChatMessage(role = "system", content = config.systemPrompt))
            }
            history.forEach { messages.add(ChatMessage(role = it.role, content = it.content)) }

            val userContent: Any = if (bitmap != null && config.modelType == ModelType.MULTIMODAL) {
                val base64 = bitmapToBase64(bitmap)
                listOf(
                    ContentPart(type = "text", text = userMessage),
                    ContentPart(type = "image_url", imageUrl = ImageUrl(url = "data:image/jpeg;base64,$base64")),
                )
            } else {
                userMessage
            }
            messages.add(ChatMessage(role = "user", content = userContent))

            val response = api.chatCompletions(
                ChatRequest(
                    model = config.modelId,
                    messages = messages,
                    maxTokens = config.maxTokens,
                    temperature = config.temperature,
                )
            )
            response.choices.firstOrNull()?.message?.content?.toString()
                ?: throw IllegalStateException("返回内容为空")
        }
    }

    fun parseWatermarkConfig(json: String, base: WatermarkConfig): WatermarkConfig {
        return runCatching {
            val jsonStart = json.indexOf('{')
            val jsonEnd = json.lastIndexOf('}')
            if (jsonStart == -1 || jsonEnd == -1) return base
            val jsonStr = json.substring(jsonStart, jsonEnd + 1)
            val map = gson.fromJson(jsonStr, Map::class.java)
            base.copy(
                showCameraModel = (map["showCameraModel"] as? Boolean) ?: base.showCameraModel,
                showLens = (map["showLens"] as? Boolean) ?: base.showLens,
                showFocalLength = (map["showFocalLength"] as? Boolean) ?: base.showFocalLength,
                showAperture = (map["showAperture"] as? Boolean) ?: base.showAperture,
                showShutterSpeed = (map["showShutterSpeed"] as? Boolean) ?: base.showShutterSpeed,
                showIso = (map["showIso"] as? Boolean) ?: base.showIso,
                showDateTime = (map["showDateTime"] as? Boolean) ?: base.showDateTime,
                showCameraLogo = (map["showCameraLogo"] as? Boolean) ?: base.showCameraLogo,
                borderStyle = runCatching {
                    win.liuping.photosuit_android.domain.model.BorderStyle.valueOf(map["borderStyle"] as String)
                }.getOrDefault(base.borderStyle),
                borderColor = parseColor(map["borderColor"] as? String) ?: base.borderColor,
                textColor = parseColor(map["textColor"] as? String) ?: base.textColor,
                accentColor = parseColor(map["accentColor"] as? String) ?: base.accentColor,
                fontScale = (map["fontScale"] as? Double)?.toFloat() ?: base.fontScale,
                cornerRadius = (map["cornerRadius"] as? Double)?.toFloat() ?: base.cornerRadius,
                paddingScale = (map["paddingScale"] as? Double)?.toFloat() ?: base.paddingScale,
                logoPosition = runCatching {
                    win.liuping.photosuit_android.domain.model.LogoPosition.valueOf(map["logoPosition"] as String)
                }.getOrDefault(base.logoPosition),
                dividerVisible = (map["dividerVisible"] as? Boolean) ?: base.dividerVisible,
            )
        }.getOrDefault(base)
    }

    private fun parseColor(hex: String?): Long? {
        if (hex == null) return null
        return runCatching {
            val clean = hex.trimStart('#')
            val full = if (clean.length == 6) "FF$clean" else clean
            java.lang.Long.parseLong(full, 16)
        }.getOrNull()
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        val scaled = if (bitmap.width > 1024 || bitmap.height > 1024) {
            val ratio = minOf(1024f / bitmap.width, 1024f / bitmap.height)
            Bitmap.createScaledBitmap(bitmap, (bitmap.width * ratio).toInt(), (bitmap.height * ratio).toInt(), true)
        } else bitmap
        scaled.compress(Bitmap.CompressFormat.JPEG, 80, baos)
        return Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP)
    }
}
