package com.lovelycatv.ai.crystalapp.service

import com.lovelycatv.ai.crystalapp.common.data.message.AbstractPromptMessage
import org.springframework.ai.chat.model.ChatResponse
import reactor.core.publisher.Flux

/**
 * @author lovelycat
 * @since 2025-04-20 02:42
 * @version 1.0
 */
interface OpenApiChatService {
    fun internalStreamChatCompletion(modelName: String, prompts: List<AbstractPromptMessage>): Flux<ChatResponse>?

    fun streamChatCompletionAsynchronicity(modelName: String, prompts: List<AbstractPromptMessage>, callback: StreamCallback)

    fun streamChatCompletion(
        sessionId: String,
        modelName: String,
        prompts: List<AbstractPromptMessage>,
        onCompleted: ((output: String, total: Int, generated: Int) -> Unit)? = null
    ): Flux<String>?

    interface StreamCallback {

        fun onReceived(token: String)

        fun onCompleted(output: String, total: Int, generated: Int)

        fun onError(e: Exception)
    }
}