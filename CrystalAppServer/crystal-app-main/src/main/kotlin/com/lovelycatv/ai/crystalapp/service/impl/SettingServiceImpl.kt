package com.lovelycatv.ai.crystalapp.service.impl

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.lovelycatv.ai.crystalapp.entity.SettingEntity
import com.lovelycatv.ai.crystalapp.mapper.SettingMapper
import com.lovelycatv.ai.crystalapp.service.SettingService
import org.springframework.stereotype.Service

/**
 * @author lovelycat
 * @since 2025-04-13 04:48
 * @version 1.0
 */
@Service
class SettingServiceImpl : SettingService, ServiceImpl<SettingMapper, SettingEntity>() {
}