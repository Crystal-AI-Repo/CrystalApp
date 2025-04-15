package com.lovelycatv.ai.crystalapp.common.utils

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.baomidou.mybatisplus.extension.service.IService
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.lovelycatv.ai.crystalapp.common.PagedData
import java.io.Serializable

/**
 * @author lovelycat
 * @since 2025-04-14 20:04
 * @version 1.0
 */
class MybatisPlusExtensions private constructor()

fun <T> IService<T?>.getOneByColumn(vararg pairs: Pair<String, Any?>): T? {
    val wrapper = QueryWrapper<T>()
    wrapper.apply {
        this.allEq(pairs.toMap())
    }
    return this.getOne(wrapper.last("LIMIT 1"))
}

fun <T> IService<T?>.listByColumn(vararg pairs: Pair<String, Any?>): List<T> {
    val wrapper = QueryWrapper<T>()
    wrapper.apply {
        this.allEq(pairs.toMap())
    }
    return this.list(wrapper).filterNotNull()
}

fun <K: Serializable, T> IService<T?>.listByIds(idColumnName: String, entityId: (T) -> K, characterIds: Array<out K>): Map<K, T?> {
    val characters = this.list(QueryWrapper<T>().`in`(idColumnName, *characterIds)).filterNotNull().associateBy { entityId.invoke(it) }
    return characterIds.associateWith { characters[it] }
}

fun <T> IService<T?>.getPagedData(
    page: Long,
    pageSize: Long,
    wrapperBuilder: QueryWrapper<T>.() -> Unit
): PagedData<T> {
    val pager = Page<T>(page, pageSize)
    val wrapper = QueryWrapper<T>()
    wrapperBuilder.invoke(wrapper)
    val result = this.page(pager, wrapper)
    return PagedData(
        total = result.total,
        pages = result.pages,
        current = result.current,
        records = result.records
    )
}