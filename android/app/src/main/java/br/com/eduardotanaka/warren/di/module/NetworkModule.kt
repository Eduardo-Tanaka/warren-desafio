package br.com.eduardotanaka.warren.di.module

import br.com.eduardotanaka.warren.data.interceptor.WarrenInterceptor
import br.com.warren.challange.BuildConfig
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * colocar as interfaces de chamadas do retrofit aqui
 */
@Module
class NetworkModule {

    @Provides
    @Singleton
    fun providesHttpLoggingInterceptor() = HttpLoggingInterceptor().apply {
        level =
            if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.NONE else HttpLoggingInterceptor.Level.BODY
    }

    @Singleton
    @Provides
    fun providesWarrenInterceptor(): WarrenInterceptor = WarrenInterceptor()

    @Singleton
    @Provides
    fun provideOkHttpClient(
        warrenInterceptor: WarrenInterceptor,
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(warrenInterceptor)
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun provideGson(): Gson {
        val gson = GsonBuilder()
        return gson.create()
    }

    @Singleton
    @Provides
    fun providesRetrofit(
        okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        val retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BuildConfig.WarrenBaseUrl)
            .addConverterFactory(
                GsonConverterFactory
                    .create(gson)
            )
        return retrofit.build()
    }

}