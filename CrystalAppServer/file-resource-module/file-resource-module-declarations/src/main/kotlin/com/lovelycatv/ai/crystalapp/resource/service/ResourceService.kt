package com.lovelycatv.ai.crystalapp.resource.service

import com.baomidou.mybatisplus.extension.service.IService
import com.lovelycatv.ai.crystalapp.common.ServiceFuncResult
import com.lovelycatv.ai.crystalapp.resource.entity.ResourceEntity
import com.lovelycatv.ai.crystalapp.resource.enums.FileResourceStorageType
import com.lovelycatv.ai.crystalapp.resource.enums.FileResourceType
import java.io.InputStream

/**
 * @author lovelycat
 * @since 2025-04-23 18:37
 * @version 1.0
 */
interface ResourceService : IService<ResourceEntity?> {
    fun saveResource(
        owner: Long,
        fileName: String,
        inputStream: InputStream,
        storageType: FileResourceStorageType,
        type: FileResourceType
    ): ServiceFuncResult<Long?>

    fun getByMd5(md5: String): ResourceEntity?
}