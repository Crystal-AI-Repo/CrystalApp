package com.lovelycatv.auth.service.impl

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.lovelycatv.auth.entity.PermissionEntity
import com.lovelycatv.auth.mapper.PermissionMapper
import com.lovelycatv.auth.service.PermissionService
import org.springframework.stereotype.Service

/**
 * @author lovelycat
 * @since 2025-04-09 19:58
 * @version 1.0
 */
@Service
class PermissionServiceImpl : PermissionService, ServiceImpl<PermissionMapper, PermissionEntity?>() {
}