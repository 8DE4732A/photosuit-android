package win.liuping.photosuit_android.data.remote.api

import retrofit2.http.Body
import retrofit2.http.POST
import win.liuping.photosuit_android.data.remote.dto.ChatRequest
import win.liuping.photosuit_android.data.remote.dto.ChatResponse

interface LlmApi {
    @POST("chat/completions")
    suspend fun chatCompletions(@Body request: ChatRequest): ChatResponse
}
