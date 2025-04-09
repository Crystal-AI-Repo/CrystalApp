package com.lovelycatv.ai.crystalapp.common

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

data class Result<T> @JsonCreator constructor(
    @JsonProperty("code")
    val code: Int,
    @JsonProperty("message")
    val message: String,
    @JsonProperty("data")
    val data: T?
) {
    @JsonIgnore
    fun isSuccessful() = this.code == 200

    companion object {
        const val RESPONSE_CODE_SUCCESS = 200
        const val RESPONSE_CODE_BAD_REQUEST = 400
        const val RESPONSE_CODE_UNAUTHORIZED = 401
        const val RESPONSE_CODE_FORBIDDEN = 403
        const val RESPONSE_CODE_INTERNAL_SERVER_ERROR = 500

        fun <T> success(message: String, data: T?) = Result(RESPONSE_CODE_SUCCESS, message, data)

        fun success(message: String) = success(message, null)

        fun <T> badRequest(message: String, data: T) = Result(RESPONSE_CODE_BAD_REQUEST, message, data)

        fun <T> unauthorized(message: String, data: T) = Result(RESPONSE_CODE_UNAUTHORIZED, message, data)

        fun <T> forbidden(message: String, data: T) = Result(RESPONSE_CODE_FORBIDDEN, message, data)

        fun <T> internalServerError(message: String, data: T) = Result(RESPONSE_CODE_INTERNAL_SERVER_ERROR, message, data)

        // Plain
        fun badRequest(message: String) = Result(RESPONSE_CODE_BAD_REQUEST, message, null)

        fun unauthorized(message: String) = Result(RESPONSE_CODE_UNAUTHORIZED, message, null)

        fun forbidden(message: String) = Result(RESPONSE_CODE_FORBIDDEN, message, null)

        fun internalServerError(message: String) = Result(RESPONSE_CODE_INTERNAL_SERVER_ERROR, message, null)

    }
}
