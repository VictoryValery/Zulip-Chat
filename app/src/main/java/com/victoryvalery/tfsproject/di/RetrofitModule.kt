package com.victoryvalery.tfsproject.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.victoryvalery.tfsproject.data.apiStorage.*
import dagger.Module
import dagger.Provides
import dagger.Reusable
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class RetrofitModule {

    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json { ignoreUnknownKeys = true }
    }

    @Provides
    @Singleton
    fun provideZulipInterceptor(): Interceptor {
        return Interceptor { chain ->
            val request = chain
                .request()
                .newBuilder()
                .addHeader("Authorization", Credentials.basic(API_USER, API_KEY))
                .build()
            var response = chain.proceed(request)
            var tryCount = 0
            while (!response.isSuccessful && tryCount < API_TRY_COUNT) {
                tryCount++
                response.close()
                Thread.sleep(API_DELAY * tryCount)
                response = chain.proceed(request)
            }
            response
        }
    }

    @Provides
    @Reusable
    fun provideOkHttpClient(zulipInterceptor: Interceptor): OkHttpClient {

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(zulipInterceptor)
            .build()
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, json: Json, apiUrlProvider: ApiUrlProvider): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(apiUrlProvider.url)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun provideZulipApi(retrofit: Retrofit): ZulipApi = retrofit.create(ZulipApi::class.java)

}
