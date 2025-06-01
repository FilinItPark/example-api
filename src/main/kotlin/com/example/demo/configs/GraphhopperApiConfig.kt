package com.example.demo.configs

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("graphopper")
class GraphhopperApiConfig(
//    @Value("\${graphopper.url:http://localhost:8080}")
    val url: String,
//    @Value("\${graphopper.token:no-key}")
    val token: String,
    val maxThreadsCount: Int,
    val enabled: Boolean,
    val showTime: Boolean
)
