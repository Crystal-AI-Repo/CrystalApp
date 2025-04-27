package com.lovelycatv.ai.crystalapp.entity

import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

/**
 * @author lovelycat
 * @since 2025-04-13 04:46
 * @version 1.0
 */
@TableName("settings")
data class SettingEntity(
    @TableId
    @TableField("name")
    var name: String,
    @TableField("display_name")
    var displayName: String,
    @TableField("description")
    var description: String,
    @TableField("value")
    var value: String
)