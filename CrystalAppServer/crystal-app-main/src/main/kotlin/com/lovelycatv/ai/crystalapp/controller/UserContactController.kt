package com.lovelycatv.ai.crystalapp.controller

import com.lovelycatv.ai.crystalapp.common.Result
import com.lovelycatv.ai.crystalapp.common.mapRecords
import com.lovelycatv.ai.crystalapp.common.transformRecords
import com.lovelycatv.ai.crystalapp.common.transformServiceFuncResult
import com.lovelycatv.ai.crystalapp.common.utils.catchException
import com.lovelycatv.ai.crystalapp.common.utils.listByIds
import com.lovelycatv.ai.crystalapp.data.BranchPath
import com.lovelycatv.ai.crystalapp.entity.ChatCharacterEntity
import com.lovelycatv.ai.crystalapp.enums.ChatTarget
import com.lovelycatv.ai.crystalapp.service.ChatCharacterService
import com.lovelycatv.ai.crystalapp.service.UserContactService
import com.lovelycatv.ai.crystalapp.vo.UserContactVO
import com.lovelycatv.auth.utils.AuthPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * @author lovelycat
 * @since 2025-04-14 19:38
 * @version 1.0
 */
@RestController
@RequestMapping("/contact")
class UserContactController(
    private val userContactService: UserContactService,
    private val chatCharacterService: ChatCharacterService
) {
    @GetMapping("/list")
    fun getUserContactList(authPrincipal: AuthPrincipal, @RequestParam("page") page: Long): Result<*> {
        return catchException {
            if (page <= 0) {
                Result.badRequest("Invalid page number")
            } else {
                val result = userContactService
                    .getUserContactList(authPrincipal.userId, page, 20, false)
                    .transformServiceFuncResult { pagedData ->
                        pagedData.mapRecords { it.toPublicVO() }
                    }

                if (result.isSuccessful()) {
                    result.transformData { pagedData ->
                        val chatCharacters = pagedData!!.records.filter {
                            it.getContactTypeEnum() == ChatTarget.CHAT_ROLE
                        }.associateBy { it.chatTargetId }

                        val chatCharacterEntities = if (chatCharacters.isEmpty())
                            emptyList()
                        else chatCharacterService.listByIds(
                            "id",
                            { it.id!! },
                            chatCharacters.values.map { it.chatTargetId }.toTypedArray()
                        ).map { (characterId, characterEntity) ->
                            chatCharacters[characterId]!! to characterEntity
                        }

                        pagedData.transformRecords {
                            chatCharacterEntities.map {
                                UserContactVO(
                                    contact = it.first,
                                    reifiedContact = it.second
                                )
                            }
                        }
                    }
                } else {
                    result
                }
            }
        }
    }

    @PostMapping("/addCharacterChat")
    fun userAddCharacterChat(authPrincipal: AuthPrincipal, @RequestParam("characterId") characterId: Long): Result<*> {
        return catchException {
            userContactService.addCharacterChat(authPrincipal.userId, characterId).transformServiceFuncResult()
        }
    }

    @PostMapping("/sendMessage")
    fun sendMessageToContact(
        authPrincipal: AuthPrincipal,
        @RequestParam("contactId")
        contactId: Long,
        @RequestParam("message")
        message: String,
        @RequestParam("branchPath")
        branchPath: String
    ): Result<*> {
        return catchException {
            userContactService.sendMessage(authPrincipal.userId, contactId, message, BranchPath(branchPath, ",")).transformServiceFuncResult()
        }
    }

    @PostMapping("/revokeMessage")
    fun revokeMessage(
        authPrincipal: AuthPrincipal,
        @RequestParam("contactId")
        contactId: Long,
        @RequestParam("messageId")
        messageId: Long
    ): Result<*> {
        return catchException {
            userContactService.revokeMessage(authPrincipal.userId, contactId, messageId).transformServiceFuncResult()
        }
    }
}