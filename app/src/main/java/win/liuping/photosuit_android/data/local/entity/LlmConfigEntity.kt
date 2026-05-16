package win.liuping.photosuit_android.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "llm_configs")
data class LlmConfigEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val apiBase: String,
    val apiKey: String,
    val modelId: String,
    val modelType: String,
    val isDefault: Boolean,
    val maxTokens: Int,
    val temperature: Float,
    val systemPrompt: String,
)
