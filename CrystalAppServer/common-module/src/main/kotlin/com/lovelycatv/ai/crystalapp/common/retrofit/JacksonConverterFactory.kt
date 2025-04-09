package com.lovelycatv.ai.crystalapp.common.retrofit

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.IOException
import java.lang.reflect.Type

class JacksonConverterFactory : Converter.Factory() {

    private val objectMapper: ObjectMapper = ObjectMapper()

    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<Annotation>,
        methodAnnotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<*, RequestBody> {
        return JacksonRequestBodyConverter<Any>(objectMapper.apply { this.registerKotlinModule() })
    }

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *> {
        return JacksonResponseBodyConverter<Any>(objectMapper, type)
    }

    class JacksonResponseBodyConverter<T>(
        private val objectMapper: ObjectMapper,
        private val type: Type
    ) : Converter<ResponseBody, T> {
        @Throws(IOException::class)
        override fun convert(value: ResponseBody): T {
            val resStr = value.string()
            // println("Remote => $resStr")
            return objectMapper.readValue(resStr, objectMapper.constructType(type))
        }
    }

    class JacksonRequestBodyConverter<T: Any>(private val objectMapper: ObjectMapper) : Converter<T, RequestBody> {
        override fun convert(value: T): RequestBody {
            // println("Convert => " + objectMapper.writeValueAsString(value))
            return RequestBody.create(null, objectMapper.writeValueAsString(value))
        }
    }

    companion object {
        fun create(): JacksonConverterFactory {
            return JacksonConverterFactory()
        }
    }
}