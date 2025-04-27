package com.lovelycatv.ai.crystalapp.controller

import com.lovelycatv.ai.crystalapp.common.mapRecords
import com.lovelycatv.ai.crystalapp.common.transformServiceFuncResult
import com.lovelycatv.ai.crystalapp.common.utils.catchException
import com.lovelycatv.ai.crystalapp.service.ChatCharacterService
import com.lovelycatv.auth.annotations.NoAuthorization
import com.lovelycatv.auth.utils.AuthPrincipal
import com.lovelycatv.auth.utils.withPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.security.Principal
import com.lovelycatv.ai.crystalapp.common.Result
import com.lovelycatv.ai.crystalapp.common.ServiceFuncResult
import com.lovelycatv.ai.crystalapp.resource.enums.FileResourceStorageType
import com.lovelycatv.ai.crystalapp.resource.enums.FileResourceType
import com.lovelycatv.ai.crystalapp.resource.service.ResourceService
import org.springframework.web.multipart.MultipartFile

/**
 * @author lovelycat
 * @since 2025-04-12 16:59
 * @version 1.0
 */
@RestController
@RequestMapping("/character")
class ChatCharacterController(
    private val chatCharacterService: ChatCharacterService,
    private val fileResourceService: ResourceService
) {
    @PostMapping("/save")
    fun addOrUpdateCharacter(
        authPrincipal: AuthPrincipal,
        @RequestParam("id", required = false)
        id: Long?,
        @RequestParam("name")
        name: String,
        @RequestParam("description")
        description: String,
        @RequestParam("model")
        model: String,
        @RequestParam("prompt")
        prompt: String,
        @RequestParam("greeting")
        greeting: String,
        @RequestParam("privacy")
        privacy: Boolean,
        @RequestParam("avatar")
        avatar: MultipartFile?
    ): Result<*> {
        val fxSave = fun (avatar: String): ServiceFuncResult<*> {
            return chatCharacterService.saveOrUpdateCharacter(
                caller = authPrincipal.userId,
                characterId = id,
                name = name,
                description = description,
                qualifiedModelName = model,
                prompt = prompt,
                greeting = greeting,
                privacy = privacy,
                avatar = avatar
            )
        }

        return catchException {
            if (avatar != null) {
                val avatarSaveResult = fileResourceService.saveResource(
                    authPrincipal.userId,
                    avatar.originalFilename!!,
                    avatar.inputStream,
                    FileResourceStorageType.LOCAL,
                    FileResourceType.AVATAR
                )

                if (avatarSaveResult.success) {
                   fxSave(avatarSaveResult.data?.toString() ?: "")
                } else {
                    avatarSaveResult
                }
            } else {
                fxSave("")
            }.transformServiceFuncResult()
        }
    }

    @PostMapping("/delete")
    fun deleteChatCharacter(authPrincipal: AuthPrincipal, @RequestParam("id") id: Long): Result<*> {
        return catchException {
            chatCharacterService.deleteChatCharacter(authPrincipal.userId, id, false).transformServiceFuncResult()
        }
    }

    @GetMapping("/details")
    fun getCharacterDetails(authPrincipal: AuthPrincipal, @RequestParam("id") characterId: Long): Result<*> {
        return catchException {
            val character = chatCharacterService.getById(characterId)
            if (character != null) {
                if (character.authorUid == authPrincipal.userId) {
                    Result.success("", character)
                } else {
                    Result.success("", character.toPublicVO())
                }
            } else {
                Result.badRequest("Character $characterId not found")
            }
        }
    }

    @GetMapping("/myCharacters")
    fun getMyCharacters(
        principal: Principal,
        @RequestParam("page")
        page: Long
    ): Result<*> {
        return catchException {
            withPrincipal(principal) {
                chatCharacterService.getUserCreatedCharacters(it.userId, page,  10, false).transformServiceFuncResult()
            }
        }
    }

    @NoAuthorization
    @GetMapping("/recentCharacters")
    fun getRecentCharacters(@RequestParam("page") page: Long): Result<*> {
        return catchException {
            chatCharacterService.getMostRecentCharacters(page, 20).transformServiceFuncResult {
                it.mapRecords { it.toPublicVO() }
            }
        }
    }
}