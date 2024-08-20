package com.exchangingcurrencyexchangetask.di

import com.exchangingcurrencyexchangetask.data.api.ExchangeRateService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://v6.exchangerate-api.com/v6/e2adccb184d9997d8b575853/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(add())
            .build()
    }

    private fun add(): OkHttpClient {
        val client = OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(
                HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY)
            )
            .build()
        return client

    }

    @Provides
    @Singleton
    fun provideExchangeRateService(retrofit: Retrofit): ExchangeRateService {
        return retrofit.create(ExchangeRateService::class.java)
    }
}
