package com.lovelycatv.ai.crystalapp.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

/**
 * @author lovelycat
 * @since 2025-04-12 01:10
 * @version 1.0
 */
@TableName("models")
data class ModelEntity(
    @TableId(type = IdType.AUTO)
    var id: Long?,
    @TableField("display_name")
    var displayName: String,
    @TableField("qualified_name")
    var qualifiedName: String,
    @TableField("context_length")
    var contextLength: Int
)