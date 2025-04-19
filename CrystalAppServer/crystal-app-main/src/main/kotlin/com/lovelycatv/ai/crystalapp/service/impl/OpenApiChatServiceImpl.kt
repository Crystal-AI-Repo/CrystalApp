package com.lovelycatv.ai.crystalapp.service.impl

import com.lovelycatv.ai.crystal.common.GlobalConstants
import com.lovelycatv.ai.crystal.common.data.message.model.chat.ChatResponseMessage
import com.lovelycatv.ai.crystal.common.response.Result
import com.lovelycatv.ai.crystal.common.util.toJSONString
import com.lovelycatv.ai.crystal.openapi.dto.StreamChatCompletionResponse
import com.lovelycatv.ai.crystal.openapi.toStreamChatCompletionResponse
import com.lovelycatv.ai.crystalapp.common.data.message.AbstractPromptMessage
import com.lovelycatv.ai.crystalapp.common.data.message.TextPromptMessage
import com.lovelycatv.ai.crystalapp.service.OpenApiChatService
import kotlinx.coroutines.launch
import org.springframework.ai.chat.messages.AssistantMessage
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.openai.OpenAiChatModel
import org.springframework.ai.openai.OpenAiChatOptions
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import java.util.*

/**
 * @author lovelycat
 * @since 2025-04-20 03:42
 * @version 1.0
 */
@Service
class OpenApiChatServiceImpl(
    private val openAiChatModel: OpenAiChatModel
) : OpenApiChatService {
    override fun internalStreamChatCompletion(modelName: String, prompts: List<AbstractPromptMessage>): Flux<ChatResponse> {
        val messages = prompts.map {
            if (it is TextPromptMessage) {
                when (it.role) {
                    AbstractPromptMessage.Role.ASSISTANT -> AssistantMessage(it.content)
                    AbstractPromptMessage.Role.USER -> UserMessage(it.content)
                    AbstractPromptMessage.Role.SYSTEM -> SystemMessage(it.content)
                }
            } else {
                throw IllegalStateException("Unsupported message type: ${it.messageType.name}")
            }
        }
        val options = OpenAiChatOptions.builder().model(modelName).build()

        return openAiChatModel.stream(Prompt(messages, options))
    }

    override fun streamChatCompletionAsynchronicity(
        modelName: String,
        prompts: List<AbstractPromptMessage>,
        callback: OpenApiChatService.StreamCallback
    ) {
        val tokens = mutableListOf<String>()
        var total = 0
        var generated = 0
        this.internalStreamChatCompletion(modelName, prompts).doOnComplete {
            callback.onCompleted(tokens.joinToString(separator = ""), total, generated)
        }.subscribe {
            callback.onReceived(it.result.output.text)
            tokens.add(it.result.output.text)
            total = it.metadata.usage.totalTokens
            generated = it.metadata.usage.completionTokens
        }
    }

    override fun streamChatCompletion(
        modelName: String,
        prompts: List<AbstractPromptMessage>,
        onCompleted: ((output: String, total: Int, generated: Int) -> Unit)?
    ): Flux<String> {
        val sessionId = UUID.randomUUID().toString()

        return Flux.create { emitter ->
            streamChatCompletionAsynchronicity(
                modelName,
                prompts,
                object : OpenApiChatService.StreamCallback {
                    override fun onReceived(token: String) {
                        emitter.next(
                            StreamChatCompletionResponse(
                                sessionId,
                                choices = listOf(
                                    StreamChatCompletionResponse.Choice(
                                        index = 0,
                                        delta = StreamChatCompletionResponse.Choice.Delta(
                                            content = token
                                        ),
                                        finishReason = null
                                    )
                                )
                            ).toJSONString()
                        )
                    }

                    override fun onCompleted(output: String, total: Int, generated: Int) {
                        onCompleted?.invoke(output, total, generated)
                        emitter.next("[DONE]")
                        emitter.complete()
                    }

                }
            )
        }
    }
}