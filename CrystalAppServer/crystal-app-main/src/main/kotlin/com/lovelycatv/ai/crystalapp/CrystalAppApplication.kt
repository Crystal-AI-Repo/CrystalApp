package com.lovelycatv.ai.crystalapp

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication(scanBasePackages = ["com.lovelycatv"])
class CrystalAppApplication

fun main(args: Array<String>) {
    SpringApplication.run(CrystalAppApplication::class.java, *args)
}
