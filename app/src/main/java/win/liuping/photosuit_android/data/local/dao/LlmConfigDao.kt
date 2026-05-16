package win.liuping.photosuit_android.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import win.liuping.photosuit_android.data.local.entity.LlmConfigEntity

@Dao
interface LlmConfigDao {
    @Query("SELECT * FROM llm_configs ORDER BY id DESC")
    fun getAll(): Flow<List<LlmConfigEntity>>

    @Query("SELECT * FROM llm_configs WHERE isDefault = 1 LIMIT 1")
    suspend fun getDefault(): LlmConfigEntity?

    @Query("SELECT * FROM llm_configs WHERE id = :id")
    suspend fun getById(id: Long): LlmConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: LlmConfigEntity): Long

    @Update
    suspend fun update(entity: LlmConfigEntity)

    @Delete
    suspend fun delete(entity: LlmConfigEntity)

    @Query("UPDATE llm_configs SET isDefault = 0")
    suspend fun clearAllDefaults()

    @Query("UPDATE llm_configs SET isDefault = 1 WHERE id = :id")
    suspend fun setDefault(id: Long)
}
