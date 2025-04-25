package com.lovelycatv.ai.crystalapp.resource.entity

import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import com.lovelycatv.ai.crystalapp.resource.enums.FileResourceStorageType
import com.lovelycatv.ai.crystalapp.resource.enums.FileResourceType

/**
 * @author lovelycat
 * @since 2025-04-23 18:35
 * @version 1.0
 */
@TableName("resources_0")
data class ResourceEntity(
    @TableId
    @TableField("id")
    var id: Long,
    @TableField("owner")
    var owner: Long,
    @TableField("storage_type")
    var storageType: Int,
    @TableField("type")
    var type: Int,
    @TableField("md5")
    var md5: String,
    @TableField("extension")
    var extension: String,
    @TableField("created_time")
    var createdTime: Long,
    @TableField("deleted")
    var deleted: Boolean
) {
    fun getStorageTypeEnum() = FileResourceStorageType.getById(this.storageType)
        ?: throw IllegalStateException("Unrecognized storage type: ${this.storageType}")

    fun getTypeEnum() = FileResourceType.getById(this.type)
        ?: throw IllegalStateException("Unrecognized resource type: ${this.type}")
}