package com.lovelycatv.ai.crystalapp.data

import com.lovelycatv.ai.crystalapp.enums.ChatMessageType

/**
 * @author lovelycat
 * @since 2025-04-14 21:01
 * @version 1.0
 */
class TextChatMessage(originalMessage: String) : AbstractChatMessage(ChatMessageType.TEXT, originalMessage)