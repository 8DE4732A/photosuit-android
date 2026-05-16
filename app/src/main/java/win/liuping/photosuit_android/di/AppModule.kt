package win.liuping.photosuit_android.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import win.liuping.photosuit_android.data.local.AppDatabase
import win.liuping.photosuit_android.data.local.dao.LlmConfigDao
import win.liuping.photosuit_android.data.local.dao.StylePresetDao
import win.liuping.photosuit_android.data.local.dao.WatermarkConfigDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME).build()

    @Provides
    fun provideWatermarkConfigDao(db: AppDatabase): WatermarkConfigDao = db.watermarkConfigDao()

    @Provides
    fun provideLlmConfigDao(db: AppDatabase): LlmConfigDao = db.llmConfigDao()

    @Provides
    fun provideStylePresetDao(db: AppDatabase): StylePresetDao = db.stylePresetDao()

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()
}
