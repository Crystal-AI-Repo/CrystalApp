package com.lovelycatv.ai.crystalapp.common.retrofit

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.time.Duration
import kotlin.reflect.KClass

/**
 * @author lovelycat
 * @since 2025-02-12 11:26
 * @version 1.0
 */
class RetrofitApi(baseUrl: String) {
    private val retrofit: Retrofit

    init {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(httpLoggingInterceptor)
                    .connectTimeout(Duration.ofSeconds(10))
                    .callTimeout(Duration.ofSeconds(10))
                    .readTimeout(Duration.ofSeconds(10))
                    .build()
            )
            .addConverterFactory(JacksonConverterFactory.create())
            .build()
    }

    fun <T : Any> getApi(clazz: KClass<T>): T = this.retrofit.create(clazz.java)
}

fun <C: CharSequence> C.toRequestBody() = this.toString().toRequestBody("application/json".toMediaType())
