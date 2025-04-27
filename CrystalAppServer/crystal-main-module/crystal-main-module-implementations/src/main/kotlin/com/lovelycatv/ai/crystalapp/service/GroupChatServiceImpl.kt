package com.lovelycatv.ai.crystalapp.service

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.lovelycatv.ai.crystalapp.entity.GroupChatEntity
import com.lovelycatv.ai.crystalapp.mapper.GroupChatMapper
import com.lovelycatv.ai.crystalapp.service.GroupChatService
import org.springframework.stereotype.Service

/**
 * @author lovelycat
 * @since 2025-04-09 22:49
 * @version 1.0
 */
@Service
class GroupChatServiceImpl : GroupChatService, ServiceImpl<GroupChatMapper, GroupChatEntity?>() {
}