package com.lovelycatv.ai.crystalapp.controller

import com.lovelycatv.ai.crystalapp.common.Result
import com.lovelycatv.ai.crystalapp.common.mapRecords
import com.lovelycatv.ai.crystalapp.common.transformServiceFuncResult
import com.lovelycatv.ai.crystalapp.common.utils.catchException
import com.lovelycatv.ai.crystalapp.service.ChatCharacterService
import com.lovelycatv.auth.annotations.NoAuthorization
import com.lovelycatv.auth.utils.withPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

/**
 * @author lovelycat
 * @since 2025-04-12 16:59
 * @version 1.0
 */
@RestController
@RequestMapping("/character")
class ChatCharacterController(
    private val chatCharacterService: ChatCharacterService
) {
    @PostMapping("/save")
    fun addOrUpdateCharacter(
        principal: Principal,
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
        greeting: String
    ): Result<*> {
        return catchException {
            withPrincipal(principal) {
                chatCharacterService.saveOrUpdateCharacter(
                    caller = it.userId,
                    characterId = id,
                    name = name,
                    description = description,
                    qualifiedModelName = model,
                    prompt = prompt,
                    greeting = greeting
                ).transformServiceFuncResult()
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
                chatCharacterService.getUserCreatedCharacters(it.userId, page, 10).transformServiceFuncResult()
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