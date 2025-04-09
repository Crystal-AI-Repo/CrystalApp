package com.lovelycatv.auth.misc

import com.fasterxml.jackson.annotation.JsonTypeInfo

/**
 * @author lovelycat
 * @since 2025-04-09 21:17
 * @version 1.0
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
class UserEntityMixin