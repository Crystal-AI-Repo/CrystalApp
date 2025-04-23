package com.lovelycatv.ai.crystalapp.service.impl

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.lovelycatv.ai.crystal.common.GlobalConstants
import com.lovelycatv.ai.crystal.common.data.message.model.chat.ChatResponseMessage
import com.lovelycatv.ai.crystal.common.response.Result
import com.lovelycatv.ai.crystal.common.util.logger
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
import reactor.core.publisher.FluxSink
import java.lang.RuntimeException
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
    private val logger = logger()

    private val objectMapper = jacksonObjectMapper().apply {
        this.setSerializationInclusion(JsonInclude.Include.NON_NULL)
    }

    override fun internalStreamChatCompletion(modelName: String, prompts: List<AbstractPromptMessage>): Flux<ChatResponse>? {
        return try {
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

            openAiChatModel.stream(Prompt(messages, options))
        } catch (e: Exception) {
            logger.error("An error occurred when calling internalStreamChatCompletion()", e)
            null
        }
    }

    override fun streamChatCompletionAsynchronicity(
        modelName: String,
        prompts: List<AbstractPromptMessage>,
        callback: OpenApiChatService.StreamCallback
    ) {
        val tokens = mutableListOf<String>()
        var total = 0
        var generated = 0

        val flux = this.internalStreamChatCompletion(modelName, prompts)
        if (flux != null) {
            flux.doOnComplete {
                callback.onCompleted(tokens.joinToString(separator = ""), total, generated)
            }.subscribe {
                callback.onReceived(it.result.output.text)
                tokens.add(it.result.output.text)
                total = it.metadata.usage.totalTokens
                generated = it.metadata.usage.completionTokens
            }
        } else {
            callback.onError(RuntimeException("Internal Server Error"))
        }
    }

    override fun streamChatCompletion(
        sessionId: String,
        modelName: String,
        prompts: List<AbstractPromptMessage>,
        onCompleted: ((output: String, total: Int, generated: Int) -> Unit)?
    ): Flux<String>? {
        val flux = this.internalStreamChatCompletion(modelName, prompts)
        if (flux != null) {
            val tokens = mutableListOf<String>()
            var total = 0
            var generated = 0

            return Flux.create { emitter ->
                flux.doOnComplete {
                    emitter.next(
                        StreamChatCompletionResponse(
                            sessionId,
                            choices = listOf(
                                StreamChatCompletionResponse.Choice(
                                    index = 0,
                                    delta = StreamChatCompletionResponse.Choice.Delta(
                                        content = ""
                                    ),
                                    finishReason = "stop"
                                )
                            ),
                            usage = StreamChatCompletionResponse.Usage(total.toLong(),(total - generated).toLong(), generated.toLong())
                        ).toJSONString(objectMapper)
                    )

                    onCompleted?.invoke(tokens.joinToString(separator = ""), total, generated)
                    emitter.next("[DONE]")
                    emitter.complete()
                }.subscribe {
                    val generatedToken = it.result.output.text

                    tokens.add(generatedToken)
                    total = it.metadata.usage.totalTokens
                    generated = it.metadata.usage.completionTokens

                    emitter.next(
                        StreamChatCompletionResponse(
                            sessionId,
                            choices = listOf(
                                StreamChatCompletionResponse.Choice(
                                    index = 0,
                                    delta = StreamChatCompletionResponse.Choice.Delta(
                                        content = generatedToken
                                    ),
                                    finishReason = null
                                )
                            )
                        ).toJSONString(objectMapper)
                    )
                }
            }
        } else {
            return null
        }
    }
}