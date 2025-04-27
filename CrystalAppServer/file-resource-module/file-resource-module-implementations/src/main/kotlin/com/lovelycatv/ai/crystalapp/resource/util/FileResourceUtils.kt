package com.lovelycatv.ai.crystalapp.resource.util

import com.lovelycatv.ai.crystalapp.common.Result
import com.lovelycatv.ai.crystalapp.common.config.CrystalAppSettings
import com.lovelycatv.ai.crystalapp.resource.entity.ResourceEntity
import com.lovelycatv.ai.crystalapp.resource.enums.FileResourceStorageType
import com.lovelycatv.ai.crystalapp.resource.enums.FileResourceType
import com.lovelycatv.ai.crystalapp.resource.service.ResourceService
import org.springframework.core.io.ClassPathResource
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

/**
 * @author lovelycat
 * @since 2025-04-23 21:45
 * @version 1.0
 */
@Component
class FileResourceUtils(
    private val crystalAppSettings: CrystalAppSettings
) {
    fun getBasePath(storageType: FileResourceStorageType, type: FileResourceType): String {
        val basePath = when (storageType) {
            FileResourceStorageType.LOCAL -> crystalAppSettings.resource.local.basePath
        }

        val fileRelativePath = when (type) {
            FileResourceType.FILE -> ""
            FileResourceType.USER_AVATAR -> crystalAppSettings.resource.local.userAvatarPath
            FileResourceType.USER_BACKGROUND -> crystalAppSettings.resource.local.userBackgroundPath
            FileResourceType.CHARACTER_AVATAR -> crystalAppSettings.resource.local.characterAvatarPath
            FileResourceType.CHARACTER_BACKGROUND -> crystalAppSettings.resource.local.characterBackgroundPath
        }

        return basePath + fileRelativePath
    }

    fun getFileBasePath(storageType: FileResourceStorageType, type: FileResourceType, md5: String): String {
        return this.getBasePath(storageType, type) + "/" + md5.chunked(2).subList(0, 3).joinToString(separator = "/", prefix = "", postfix = "")
    }

    fun getFileBasePath(resourceEntity: ResourceEntity): String {
        return this.getFileBasePath(resourceEntity.getStorageTypeEnum(), resourceEntity.getTypeEnum(), resourceEntity.md5)
    }

    fun getFilePath(resourceEntity: ResourceEntity): String {
        return this.getFileBasePath(resourceEntity.getStorageTypeEnum(), resourceEntity.getTypeEnum(), resourceEntity.md5) + "/${resourceEntity.md5}.${resourceEntity.extension}"
    }
}