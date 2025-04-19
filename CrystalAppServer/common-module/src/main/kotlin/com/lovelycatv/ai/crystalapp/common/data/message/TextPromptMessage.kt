package com.lovelycatv.ai.crystalapp.common.data.message

/**
 * @author lovelycat
 * @since 2025-04-20 03:46
 * @version 1.0
 */
class TextPromptMessage(role: Role, val content: String) : AbstractPromptMessage(Type.TEXT, role)