package com.lovelycatv.ai.crystalapp.entity

import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

/**
 * @author lovelycat
 * @since 2025-04-14 20:32
 * @version 1.0
 */
@TableName("chat_history_relations_0")
data class ChatHistoryMessageRelationEntity(
    @TableField("current_id")
    val currentId: Long,
    @TableId
    @TableField("next_id")
    val nextId: Long,
    @TableField("order_no")
    val orderNo: Int
)