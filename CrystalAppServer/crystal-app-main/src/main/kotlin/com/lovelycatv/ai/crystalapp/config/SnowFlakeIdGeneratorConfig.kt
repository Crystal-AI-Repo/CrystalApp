package com.lovelycatv.ai.crystalapp.config

import com.lovelycatv.ai.crystalapp.common.utils.SnowIdGenerator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @author lovelycat
 * @since 2025-04-14 21:10
 * @version 1.0
 */
@Configuration
class SnowFlakeIdGeneratorConfig {
    @Bean("chatHistoryMessageIdGenerator")
    fun chatHistoryMessageIdGenerator(): SnowIdGenerator {
        return SnowIdGenerator(0L, 41, 5, 5, 12, 0, 0, 0, 0)
    }
}