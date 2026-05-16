package win.liuping.photosuit_android.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import win.liuping.photosuit_android.data.local.dao.LlmConfigDao
import win.liuping.photosuit_android.data.local.dao.StylePresetDao
import win.liuping.photosuit_android.data.local.dao.WatermarkConfigDao
import win.liuping.photosuit_android.data.local.entity.LlmConfigEntity
import win.liuping.photosuit_android.data.local.entity.StylePresetEntity
import win.liuping.photosuit_android.data.local.entity.WatermarkConfigEntity

@Database(
    entities = [WatermarkConfigEntity::class, LlmConfigEntity::class, StylePresetEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun watermarkConfigDao(): WatermarkConfigDao
    abstract fun llmConfigDao(): LlmConfigDao
    abstract fun stylePresetDao(): StylePresetDao

    companion object {
        const val DATABASE_NAME = "photosuit.db"
    }
}
