package com.fastthinkerstudios.lastminutegenius.di

import android.app.Application
import androidx.room.Room
import com.fastthinkerstudios.lastminutegenius.data.local.AppDatabase
import com.fastthinkerstudios.lastminutegenius.data.processor.VideoProcessor
import com.fastthinkerstudios.lastminutegenius.data.remote.SummaryApi
import com.fastthinkerstudios.lastminutegenius.data.repository.CategoryRepositoryImpl
import com.fastthinkerstudios.lastminutegenius.data.repository.SummaryRepositoryImpl
import com.fastthinkerstudios.lastminutegenius.data.repository.VideoRepositoryImpl
import com.fastthinkerstudios.lastminutegenius.domain.repository.CategoryRepository
import com.fastthinkerstudios.lastminutegenius.domain.repository.VideoRepository
import com.fastthinkerstudios.lastminutegenius.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(application: Application): AppDatabase{
        return Room.databaseBuilder(application, AppDatabase::class.java, "VideoSummariesDatabase").build()
    }


    @Provides
    @Singleton
    fun provideCategoryRepository(db: AppDatabase): CategoryRepository{
        return CategoryRepositoryImpl(db.categoryDao())
    }

    @Provides
    @Singleton
    fun provideVideoRepository(db: AppDatabase): VideoRepository{
        return VideoRepositoryImpl(db.videoDao())
    }

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.MINUTES)  // Bağlantı zaman aşımı
            .readTimeout(60, TimeUnit.MINUTES)     // Okuma zaman aşımı
            .writeTimeout(60, TimeUnit.MINUTES)    // Yazma zaman aşımı
            .build()

        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)   // Buraya ekledik
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideSummaryApi(retrofit: Retrofit): SummaryApi {
        return retrofit.create(SummaryApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSummaryRepository(api: SummaryApi): SummaryRepositoryImpl{
        return SummaryRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideVideoProcessor(): VideoProcessor{
        return VideoProcessor()
    }

}