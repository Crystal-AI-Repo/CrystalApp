package com.lovelycatv.ai.crystalapp.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

/**
 * @author lovelycat
 * @since 2025-04-08 23:03
 * @version 1.0
 */
@RestController
class TestController {
    @GetMapping("/hello")
    fun get(principal: Principal): String? {
        return ObjectMapper().writeValueAsString(principal)
    }
}