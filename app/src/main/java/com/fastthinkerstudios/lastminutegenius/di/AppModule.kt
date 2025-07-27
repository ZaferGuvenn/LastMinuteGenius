package com.fastthinkerstudios.lastminutegenius.di

import com.fastthinkerstudios.lastminutegenius.data.processor.VideoProcessor
import com.fastthinkerstudios.lastminutegenius.data.remote.SummaryApi
import com.fastthinkerstudios.lastminutegenius.data.remote.SummaryRepository
import com.fastthinkerstudios.lastminutegenius.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
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