package win.liuping.photosuit_android.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import win.liuping.photosuit_android.data.local.entity.WatermarkConfigEntity

@Dao
interface WatermarkConfigDao {
    @Query("SELECT * FROM watermark_configs ORDER BY id DESC")
    fun getAll(): Flow<List<WatermarkConfigEntity>>

    @Query("SELECT * FROM watermark_configs WHERE id = :id")
    suspend fun getById(id: Long): WatermarkConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: WatermarkConfigEntity): Long

    @Update
    suspend fun update(entity: WatermarkConfigEntity)

    @Delete
    suspend fun delete(entity: WatermarkConfigEntity)

    @Query("DELETE FROM watermark_configs WHERE id = :id")
    suspend fun deleteById(id: Long)
}
