package com.lovelycatv.ai.crystalapp.service

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.lovelycatv.ai.crystalapp.entity.GroupMemberEntity
import com.lovelycatv.ai.crystalapp.mapper.GroupMemberMapper
import com.lovelycatv.ai.crystalapp.service.GroupMemberService
import org.springframework.stereotype.Service

/**
 * @author lovelycat
 * @since 2025-04-09 22:50
 * @version 1.0
 */
@Service
class GroupMemberServiceImpl : GroupMemberService, ServiceImpl<GroupMemberMapper, GroupMemberEntity?>() {
}