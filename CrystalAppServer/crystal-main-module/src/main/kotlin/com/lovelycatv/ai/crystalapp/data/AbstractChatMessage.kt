package com.lovelycatv.ai.crystalapp.data

import com.lovelycatv.ai.crystalapp.enums.ChatMessageType

/**
 * @author lovelycat
 * @since 2025-04-14 21:00
 * @version 1.0
 */
abstract class AbstractChatMessage(
    val messageType: ChatMessageType,
    val originalMessage: String
)