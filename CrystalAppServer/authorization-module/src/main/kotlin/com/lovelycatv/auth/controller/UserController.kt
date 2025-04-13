package com.lovelycatv.auth.controller

import com.lovelycatv.ai.crystalapp.common.Result
import com.lovelycatv.ai.crystalapp.common.transformServiceFuncResult
import com.lovelycatv.ai.crystalapp.common.utils.catchException
import com.lovelycatv.auth.annotations.NoAuthorization
import com.lovelycatv.auth.dto.UpdateProfileDTO
import com.lovelycatv.auth.service.UserService
import com.lovelycatv.auth.utils.AuthPrincipal
import com.lovelycatv.auth.utils.withPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

/**
 * @author lovelycat
 * @since 2025-04-13 15:31
 * @version 1.0
 */
@RestController
@RequestMapping("/user")
class UserController(
    private val userService: UserService
) {
    @NoAuthorization
    @GetMapping("/profile")
    fun getUserProfile(@RequestParam("uid") uid: Long): Result<*> {
        return catchException {
            val user = userService.getById(uid)
            if (user != null) {
                Result.success(user.username, user.apply { desensitize() }.toPublicVO())
            } else {
                Result.badRequest("User $uid not found")
            }
        }
    }

    @GetMapping("/myProfile")
    fun getMyProfile(authPrincipal: AuthPrincipal): Result<*> {
        return catchException {
            val user = userService.getById(authPrincipal.userId)
            if (user != null) {
                Result.success(authPrincipal.username, user.apply { desensitize() })
            } else {
                Result.badRequest("Invalid uid ${authPrincipal.userId}, username: ${authPrincipal.username}")
            }
        }
    }

    @PostMapping("/myProfile")
    fun updateMyProfile(authPrincipal: AuthPrincipal, @RequestBody dto: UpdateProfileDTO): Result<*> {
        return catchException {
            userService.updateProfile(authPrincipal.userId, dto).transformServiceFuncResult()
        }
    }
}