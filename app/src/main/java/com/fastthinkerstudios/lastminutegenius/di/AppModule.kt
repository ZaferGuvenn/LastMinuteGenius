package com.fastthinkerstudios.lastminutegenius.di

import com.fastthinkerstudios.lastminutegenius.data.processor.VideoProcessor
import com.fastthinkerstudios.lastminutegenius.data.remote.SummaryApi
import com.fastthinkerstudios.lastminutegenius.data.remote.SummaryRepository
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
    fun provideRetrofit(): Retrofit {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(3, TimeUnit.MINUTES)  // Bağlantı zaman aşımı
            .readTimeout(3, TimeUnit.MINUTES)     // Okuma zaman aşımı
            .writeTimeout(3, TimeUnit.MINUTES)    // Yazma zaman aşımı
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
    fun provideSummaryRepository(api: SummaryApi): SummaryRepository{
        return SummaryRepository(api)
    }

    @Provides
    @Singleton
    fun provideVideoProcessor(): VideoProcessor{
        return VideoProcessor()
    }

}