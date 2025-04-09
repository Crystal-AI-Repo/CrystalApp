package com.lovelycatv.ai.crystalapp

import org.mybatis.spring.annotation.MapperScan
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication(scanBasePackages = ["com.lovelycatv"])
@MapperScan(value = ["com.lovelycatv.auth.mapper"])
class CrystalAppApplication

fun main(args: Array<String>) {
    SpringApplication.run(CrystalAppApplication::class.java, *args)
}
