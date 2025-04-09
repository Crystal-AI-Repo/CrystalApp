package com.lovelycatv.ai.crystalapp.common.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

/**
 * @author lovelycat
 * @since 2025-02-15 20:55
 * @version 1.0
 */
class JacksonJSONExtensions private constructor()

fun <T> T?.toJSONString(mapper: ObjectMapper = ObjectMapper()) = mapper.registerKotlinModule().writeValueAsString(this)

inline fun <C: CharSequence, reified T> C.toExplicitObject(mapper: ObjectMapper = ObjectMapper()): T {
    return mapper.registerKotlinModule().readValue(this.toString(), T::class.java)
}