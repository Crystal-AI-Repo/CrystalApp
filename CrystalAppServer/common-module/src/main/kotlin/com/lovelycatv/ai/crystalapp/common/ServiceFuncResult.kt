package com.lovelycatv.ai.crystalapp.common

/**
 * @author lovelycat
 * @since 2025-04-13 03:09
 * @version 1.0
 */
data class ServiceFuncResult<T>(
    val success: Boolean,
    val message: String,
    val data: T
) {
    companion object {
        fun <T> success(message: String, data: T) = ServiceFuncResult(true, message, data)

        fun success(message: String) = ServiceFuncResult(true, message, null)

        fun failed(message: String) = ServiceFuncResult(false, message, null)

        fun notResourceOwner() = ServiceFuncResult.failed("You are not the owner of this resource")
    }
}

fun <T> ServiceFuncResult<T>.transformServiceFuncResult(): Result<*> {
    return this.transformServiceFuncResult<T, Nothing>()
}

fun <T, R> ServiceFuncResult<T>.transformServiceFuncResult(dataProcessor: ((T) -> R)? = null): Result<*> {
    return if (this.success) {
        Result.success(
            message = this.message,
            data = if (dataProcessor != null)
                dataProcessor.invoke(this.data)
            else
                this.data
        )
    } else {
        Result.badRequest(this.message)
    }
}
