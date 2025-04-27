package com.lovelycatv.ai.crystalapp.resource.controller

import com.lovelycatv.ai.crystalapp.common.Result
import com.lovelycatv.ai.crystalapp.common.transformServiceFuncResult
import com.lovelycatv.ai.crystalapp.common.utils.rollbackTransaction
import com.lovelycatv.ai.crystalapp.resource.enums.FileResourceStorageType
import com.lovelycatv.ai.crystalapp.resource.enums.FileResourceType
import com.lovelycatv.ai.crystalapp.resource.service.ResourceService
import com.lovelycatv.auth.service.UserService
import com.lovelycatv.auth.utils.AuthPrincipal
import org.springframework.core.io.ClassPathResource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

/**
 * @author lovelycat
 * @since 2025-04-24 17:17
 * @version 1.0
 */
@RestController
@RequestMapping("/resource/user")
class UserResourceController(
    private val resourceService: ResourceService,
    private val userService: UserService,
    private val resourceController: ResourceController
) {
    @GetMapping("/avatar")
    fun getUserAvatar(@RequestParam("uid") userId: Long): Any? {
        val user = userService.getById(userId) ?: return Result.badRequest("User $userId not found")
        return  if (user.avatar.isNotBlank()) {
            resourceController.getResource(user.avatar.toLong())
        } else {
            ResponseEntity.ok()
                .header("Content-Type", "image/png")
                .body(ClassPathResource("/static/images/akarin.png"))
        }
    }


    @PostMapping("/uploadAvatar")
    fun uploadAvatar(authPrincipal: AuthPrincipal, @RequestParam("file") file: MultipartFile): Result<*> {
        val uploadResult = resourceService.saveResource(
            authPrincipal.userId, file.originalFilename!!, file.inputStream, FileResourceStorageType.LOCAL, FileResourceType.AVATAR
        )

        return if (uploadResult.success) {
            val updateResult = userService.updateAvatar(authPrincipal.userId, uploadResult.data!!)
            if (updateResult.success) {
                updateResult
            } else {
                // Rollback
                rollbackTransaction()

                updateResult
            }
        } else {
            // Rollback
            rollbackTransaction()

            uploadResult
        }.transformServiceFuncResult()
    }
}