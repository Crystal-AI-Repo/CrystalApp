package com.lovelycatv.ai.crystalapp.controller

import com.lovelycatv.ai.crystalapp.common.Result
import com.lovelycatv.ai.crystalapp.common.transform
import com.lovelycatv.ai.crystalapp.common.transformServiceFuncResult
import com.lovelycatv.ai.crystalapp.common.utils.catchException
import com.lovelycatv.ai.crystalapp.entity.ChatHistoryMessageEntity
import com.lovelycatv.ai.crystalapp.service.ChatHistoryMessageService
import com.lovelycatv.ai.crystalapp.service.UserContactService
import com.lovelycatv.auth.utils.AuthPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * @author lovelycat
 * @since 2025-04-19 19:30
 * @version 1.0
 */
@RestController
@RequestMapping("/chat-history")
class ChatHistoryController(
    private val userContactService: UserContactService,
    private val chatHistoryMessageService: ChatHistoryMessageService
) {
    @GetMapping("/fetch")
    fun fetchChatHistory(
        authPrincipal: AuthPrincipal,
        @RequestParam("contactId")
        contactId: Long,
        @RequestParam("messageId")
        messageId: Long
    ): Result<*> = catchException {
        // Validate message root
        val validateResult = userContactService.validateMessageRoot(authPrincipal.userId, contactId, messageId)
        if (validateResult.success) {
            chatHistoryMessageService.fetchHistoryMessagesUpwards(messageId, 20).transform {
                it.filter { it.isAvailable() }
            }
        } else {
            validateResult
        }.transformServiceFuncResult()
    }

    @GetMapping("/leaves")
    fun getHistoryMessageLeaves(
        authPrincipal: AuthPrincipal,
        @RequestParam("contactId")
        contactId: Long
    ): Result<*> = catchException {
        val contact = userContactService.validateContactOwner(authPrincipal.userId, contactId)
        if (contact == null) {
            Result.badRequest("Contact not found")
        } else {
            val tree = chatHistoryMessageService.getFullMessageHistoryTree(contact.chatHistoryStart)
            if (tree.success) {
                Result.success("", tree.data!!.findAllAvailableLeaves(ChatHistoryMessageEntity.nodeFilter).map { it.last() })
            } else {
                tree.transformServiceFuncResult()
            }
        }
    }
}