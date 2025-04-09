package com.lovelycatv.ai.crystalapp.service.impl

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.lovelycatv.ai.crystalapp.entity.UserContactEntity
import com.lovelycatv.ai.crystalapp.mapper.UserContactMapper
import com.lovelycatv.ai.crystalapp.service.UserContactService
import org.springframework.stereotype.Service

/**
 * @author lovelycat
 * @since 2025-04-09 22:51
 * @version 1.0
 */
@Service
class UserContactServiceImpl : UserContactService, ServiceImpl<UserContactMapper, UserContactEntity?>() {
}