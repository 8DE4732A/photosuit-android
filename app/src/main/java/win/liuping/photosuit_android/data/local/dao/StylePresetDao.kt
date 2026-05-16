package win.liuping.photosuit_android.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import win.liuping.photosuit_android.data.local.entity.StylePresetEntity

@Dao
interface StylePresetDao {
    @Query("SELECT * FROM style_presets ORDER BY createdAt DESC")
    fun getAll(): Flow<List<StylePresetEntity>>

    @Query("SELECT * FROM style_presets WHERE id = :id")
    suspend fun getById(id: Long): StylePresetEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: StylePresetEntity): Long

    @Update
    suspend fun update(entity: StylePresetEntity)

    @Delete
    suspend fun delete(entity: StylePresetEntity)

    @Query("DELETE FROM style_presets WHERE id = :id")
    suspend fun deleteById(id: Long)
}
