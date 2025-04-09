package com.lovelycatv.ai.crystalapp.service.impl

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.lovelycatv.ai.crystalapp.entity.ChatRoleEntity
import com.lovelycatv.ai.crystalapp.mapper.ChatRoleMapper
import com.lovelycatv.ai.crystalapp.service.ChatRoleService
import org.springframework.stereotype.Service

/**
 * @author lovelycat
 * @since 2025-04-09 22:48
 * @version 1.0
 */
@Service
class ChatRoleServiceImpl : ChatRoleService, ServiceImpl<ChatRoleMapper, ChatRoleEntity?>() {

}