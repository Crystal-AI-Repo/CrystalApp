package com.lovelycatv.ai.crystalapp.controller

import com.lovelycatv.ai.crystalapp.common.Result
import com.lovelycatv.ai.crystalapp.common.utils.catchException
import com.lovelycatv.ai.crystalapp.service.ModelService
import org.springframework.dao.DuplicateKeyException
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authorization.method.PreAuthorizeAuthorizationManager
import org.springframework.security.config.method.MethodSecurityBeanDefinitionParser.PreAuthorizeAuthorizationMethodInterceptor
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

/**
 * @author lovelycat
 * @since 2025-04-12 01:23
 * @version 1.0
 */
@RestController
@RequestMapping("/model")
class ModelManagerController(
    private val modelService: ModelService
) {
    @GetMapping("/list")
    fun getModelList(): Result<*> {
        return Result.success("", modelService.list())
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('models.manage')")
    fun addModel(
        @RequestParam("modelName")
        modelName: String,
        @RequestParam("qualifiedName")
        qualifiedName: String,
        @RequestParam("contextLength")
        contextLength: Int
    ): Result<*> {
        if (modelName.isBlank()) {
            return Result.badRequest("ModelName could not be empty")
        }

        if (qualifiedName.isBlank()) {
            return Result.badRequest("QualifiedName could not be empty")
        }

        if (contextLength <= 0) {
            return Result.badRequest("ContextLength could not less than 1")
        }

        return catchException(
            onException = {
                if (it is DuplicateKeyException) {
                    Result.badRequest("$qualifiedName is already exists")
                } else {
                    null
                }
            }
        ) {
            if (modelService.addOrUpdateNewModel(modelName, qualifiedName, contextLength))
                Result.success("$modelName ($qualifiedName) was successfully saved")
            else
                Result.badRequest("Request failed")
        }
    }

    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('models.manage')")
    fun addModel(
        @RequestParam("qualifiedName")
        qualifiedName: String
    ): Result<*> {
        if (qualifiedName.isBlank()) {
            return Result.badRequest("QualifiedName could not be empty")
        }

        return catchException {
            if (modelService.deleteModel(qualifiedName))
                Result.success("$qualifiedName was successfully deleted")
            else
                Result.badRequest("Request failed")
        }
    }
}