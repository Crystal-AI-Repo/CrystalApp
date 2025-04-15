package com.lovelycatv.ai.crystalapp.service.impl

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.lovelycatv.ai.crystalapp.entity.ChatHistoryMessageRelationEntity
import com.lovelycatv.ai.crystalapp.mapper.ChatHistoryMessageRelationMapper
import com.lovelycatv.ai.crystalapp.service.ChatHistoryMessageRelationService
import org.springframework.stereotype.Service

/**
 * @author lovelycat
 * @since 2025-04-14 20:38
 * @version 1.0
 */
@Service
class ChatHistoryMessageRelationServiceImpl : ChatHistoryMessageRelationService, ServiceImpl<ChatHistoryMessageRelationMapper, ChatHistoryMessageRelationEntity?>() {
}