package com.lovelycatv.ai.crystalapp.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.lovelycatv.ai.crystalapp.entity.ModelEntity
import com.lovelycatv.ai.crystalapp.mapper.ModelMapper
import com.lovelycatv.ai.crystalapp.service.ModelService
import org.springframework.stereotype.Service

/**
 * @author lovelycat
 * @since 2025-04-12 01:12
 * @version 1.0
 */
@Service
class ModelServiceImpl : ModelService, ServiceImpl<ModelMapper, ModelEntity?>() {
    override fun addOrUpdateNewModel(modelName: String, qualifiedName: String, contextLength: Int): Boolean {
        val exist = this.getByQualifiedName(qualifiedName)
        return if (exist == null) {
            save(ModelEntity(null, modelName, qualifiedName, contextLength))
        } else {
            updateById(exist.apply {
                this.displayName = modelName
                this.qualifiedName = qualifiedName
                this.contextLength = contextLength
            })
        }

    }

    override fun getByQualifiedName(qualifiedName: String): ModelEntity? {
        return getOne(QueryWrapper<ModelEntity>().eq("qualified_name", qualifiedName))
    }

    override fun deleteModel(qualifiedName: String): Boolean {
        return remove(QueryWrapper<ModelEntity>().eq("qualified_name", qualifiedName))
    }
}