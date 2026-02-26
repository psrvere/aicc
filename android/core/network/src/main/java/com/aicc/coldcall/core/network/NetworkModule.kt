package com.aicc.coldcall.core.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideMoshi() = MoshiFactory.create()

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        baseUrlProvider: BaseUrlProvider
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrlProvider.getBaseUrl())
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(MoshiFactory.create()))
            .build()

    @Provides
    @Singleton
    fun provideAiccApiService(retrofit: Retrofit): AiccApiService =
        retrofit.create(AiccApiService::class.java)
}
