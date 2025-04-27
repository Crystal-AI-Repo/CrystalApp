package com.lovelycatv.ai.crystalapp.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.lovelycatv.ai.crystalapp.entity.ModelEntity
import org.apache.ibatis.annotations.Mapper

/**
 * @author lovelycat
 * @since 2025-04-12 01:12
 * @version 1.0
 */
@Mapper
interface ModelMapper : BaseMapper<ModelEntity?> {
}