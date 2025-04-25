package com.lovelycatv.ai.crystalapp

import org.mybatis.spring.annotation.MapperScan
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity

@SpringBootApplication(scanBasePackages = ["com.lovelycatv"])
@MapperScan(value = ["com.lovelycatv.auth.mapper", "com.lovelycatv.ai.crystalapp.mapper", "com.lovelycatv.ai.crystalapp.resource.mapper"])
@EnableMethodSecurity(prePostEnabled = true)
class CrystalAppApplication

fun main(args: Array<String>) {
    SpringApplication.run(CrystalAppApplication::class.java, *args)
}
