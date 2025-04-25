package com.lovelycatv.ai.crystalapp.resource.service

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.lovelycatv.ai.crystalapp.common.ServiceFuncResult
import com.lovelycatv.ai.crystalapp.common.config.CrystalAppSettings
import com.lovelycatv.ai.crystalapp.common.utils.SnowIdGenerator
import com.lovelycatv.ai.crystalapp.common.utils.getOneByColumn
import com.lovelycatv.ai.crystalapp.common.utils.logger
import com.lovelycatv.ai.crystalapp.resource.entity.ResourceEntity
import com.lovelycatv.ai.crystalapp.resource.enums.FileResourceStorageType
import com.lovelycatv.ai.crystalapp.resource.enums.FileResourceType
import com.lovelycatv.ai.crystalapp.resource.mapper.ResourceMapper
import com.lovelycatv.ai.crystalapp.resource.util.FileResourceUtils
import jakarta.annotation.Resource
import org.springframework.stereotype.Service
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.security.MessageDigest


/**
 * @author lovelycat
 * @since 2025-04-23 18:37
 * @version 1.0
 */
@Service
class ResourceServiceImpl(
    private val crystalAppSettings: CrystalAppSettings,
    @Resource(name = "resourceIdGenerator")
    private val resourceIdGenerator: SnowIdGenerator,
    private val fileResourceUtils: FileResourceUtils
) : ResourceService, ServiceImpl<ResourceMapper, ResourceEntity?>() {
    private val logger = logger()

    @OptIn(ExperimentalStdlibApi::class)
    override fun saveResource(
        owner: Long,
        fileName: String,
        inputStream: InputStream,
        storageType: FileResourceStorageType,
        type: FileResourceType
    ): ServiceFuncResult<Long?> {
        return try {
            val id = resourceIdGenerator.nextId(0L)
            val (pureFileName, extension) = fileName.split(".")

            val basePath = when (storageType) {
                FileResourceStorageType.LOCAL -> crystalAppSettings.resource.local.basePath
            }

            val fileRelativePath = when (type) {
                FileResourceType.FILE -> ""
                FileResourceType.AVATAR -> crystalAppSettings.resource.local.avatarPath
            }

            val fileData = inputStream.use { it.readBytes() }

            val md5 = MessageDigest.getInstance("MD5")
                .digest(fileData)
                .toHexString()

            val existing = this.getByMd5(md5)
            if (existing != null) {
                return ServiceFuncResult.success("Duplicated Resource", existing.id)
            }

            val actualBasePath = fileResourceUtils.getFileBasePath(storageType, type, md5)

            File(actualBasePath).mkdirs()

            val targetFile = File("$actualBasePath/$md5.$extension")
            targetFile.writeBytes(fileData)
            if (targetFile.length() > 0) {
                val saveResult = save(
                    ResourceEntity(
                    id = id,
                    owner = owner,
                    storageType = storageType.typeId,
                    type = type.typeId,
                    md5 = md5,
                    extension = extension,
                    createdTime = System.currentTimeMillis(),
                    deleted = false
                ))

                if (saveResult) {
                    ServiceFuncResult.success("", id)
                } else {
                    ServiceFuncResult.failedWithData("Could not save file metadata to database")
                }
            } else {
                ServiceFuncResult.failedWithData("Could not save file, copy failed.")
            }
        } catch (e: IOException) {
            logger.error("Could not save file: ${e.message}, user: $owner, storage: ${storageType.name}, type: ${type.name}", e)
            ServiceFuncResult.failedWithData("Could not save file: " + e.message)
        }
    }

    override fun getByMd5(md5: String): ResourceEntity? {
        return this.getOneByColumn("md5" to md5)
    }
}