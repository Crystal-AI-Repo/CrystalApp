package com.lovelycatv.ai.crystalapp.resource.controller

import com.lovelycatv.ai.crystalapp.common.Result
import com.lovelycatv.ai.crystalapp.service.ChatCharacterService
import org.springframework.core.io.ClassPathResource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * @author lovelycat
 * @since 2025-04-24 17:47
 * @version 1.0
 */
@RestController
@RequestMapping("/resource/character")
class ChatCharacterResourceController(
    private val resourceController: ResourceController,
    private val characterService: ChatCharacterService
) {
    @GetMapping("/avatar")
    fun getCharacterAvatar(@RequestParam("id") characterId: Long): Any? {
        val character = characterService.getById(characterId) ?: return Result.badRequest("Character $characterId not found")
        return if (character.avatar.isNotBlank()) {
            resourceController.getResource(character.avatar.toLong())
        } else {
            ResponseEntity.ok()
                .header("Content-Type", "image/png")
                .body(ClassPathResource("/static/images/akarin.png"))
        }
    }

}