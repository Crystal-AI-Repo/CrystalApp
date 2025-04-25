package com.lovelycatv.ai.crystalapp.resource.controller


import com.lovelycatv.ai.crystalapp.common.Result
import com.lovelycatv.ai.crystalapp.common.transformServiceFuncResult
import com.lovelycatv.ai.crystalapp.common.utils.rollbackTransaction
import com.lovelycatv.ai.crystalapp.resource.enums.FileResourceStorageType
import com.lovelycatv.ai.crystalapp.resource.enums.FileResourceType
import com.lovelycatv.ai.crystalapp.resource.service.ResourceService
import com.lovelycatv.ai.crystalapp.resource.util.FileResourceUtils
import com.lovelycatv.auth.service.UserService
import com.lovelycatv.auth.utils.AuthPrincipal
import org.springframework.core.io.UrlResource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Paths


/**
 * @author lovelycat
 * @since 2025-04-23 18:17
 * @version 1.0
 */
@RestController
@RequestMapping("/resource")
class ResourceController(
    private val resourceService: ResourceService,
    private val fileResourceUtils: FileResourceUtils
) {
    @GetMapping("/view/{fileId}")
    fun getResource(@PathVariable("fileId") fileId: Long): Any {
        val resource = resourceService.getById(fileId) ?: return Result.badRequest("Resource $fileId not found")

        val path = Paths.get(fileResourceUtils.getFilePath(resource))
        val res = UrlResource(path.toUri())

        return ResponseEntity.ok().header("Content-Type", "image/png").body(res)
    }

    @PostMapping("/upload")
    fun uploadFileResource(authPrincipal: AuthPrincipal, @RequestParam("file") file: MultipartFile): Result<*> {
        return resourceService.saveResource(
            authPrincipal.userId, file.originalFilename!!, file.inputStream, FileResourceStorageType.LOCAL, FileResourceType.FILE
        ).transformServiceFuncResult()
    }
}