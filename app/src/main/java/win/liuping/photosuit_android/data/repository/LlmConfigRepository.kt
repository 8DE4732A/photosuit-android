package win.liuping.photosuit_android.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import win.liuping.photosuit_android.data.local.dao.LlmConfigDao
import win.liuping.photosuit_android.data.local.entity.LlmConfigEntity
import win.liuping.photosuit_android.domain.model.LlmConfig
import win.liuping.photosuit_android.domain.model.ModelType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LlmConfigRepository @Inject constructor(
    private val dao: LlmConfigDao,
) {
    fun getAll(): Flow<List<LlmConfig>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    suspend fun getDefault(): LlmConfig? = dao.getDefault()?.toDomain()

    suspend fun getById(id: Long): LlmConfig? = dao.getById(id)?.toDomain()

    suspend fun save(config: LlmConfig): Long = dao.insert(config.toEntity())

    suspend fun update(config: LlmConfig) = dao.update(config.toEntity())

    suspend fun delete(config: LlmConfig) = dao.delete(config.toEntity())

    suspend fun setDefault(id: Long) {
        dao.clearAllDefaults()
        dao.setDefault(id)
    }
}

private fun LlmConfigEntity.toDomain() = LlmConfig(
    id = id,
    name = name,
    apiBase = apiBase,
    apiKey = apiKey,
    modelId = modelId,
    modelType = ModelType.valueOf(modelType),
    isDefault = isDefault,
    maxTokens = maxTokens,
    temperature = temperature,
    systemPrompt = systemPrompt,
)

private fun LlmConfig.toEntity() = LlmConfigEntity(
    id = id,
    name = name,
    apiBase = apiBase,
    apiKey = apiKey,
    modelId = modelId,
    modelType = modelType.name,
    isDefault = isDefault,
    maxTokens = maxTokens,
    temperature = temperature,
    systemPrompt = systemPrompt,
)
