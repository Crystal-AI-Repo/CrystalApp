package com.lovelycatv.ai.crystalapp.service

import com.baomidou.mybatisplus.extension.service.IService
import com.lovelycatv.ai.crystalapp.entity.ModelEntity

/**
 * @author lovelycat
 * @since 2025-04-12 01:12
 * @version 1.0
 */
interface ModelService : IService<ModelEntity?> {
    fun addOrUpdateNewModel(modelName: String, qualifiedName: String, contextLength: Int): Boolean

    fun getByQualifiedName(qualifiedName: String): ModelEntity?

    fun deleteModel(qualifiedName: String): Boolean
}