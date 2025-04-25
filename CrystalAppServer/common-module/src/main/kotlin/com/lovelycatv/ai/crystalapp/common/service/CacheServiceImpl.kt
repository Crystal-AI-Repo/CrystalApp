package com.lovelycatv.ai.crystalapp.common.service

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import java.io.Serializable

/**
 * @author lovelycat
 * @since 2025-04-26 03:01
 * @version 1.0
 */
abstract class CacheServiceImpl<M: BaseMapper<T>, T> : ICacheService<T>, ServiceImpl<M, T>() {
    override fun originalGetById(id: Serializable): T {
        return super.baseMapper.selectById(id)
    }

    override fun originalListByIds(id: Collection<Serializable>): List<T> {
        return super.baseMapper.selectBatchIds(id)
    }
}