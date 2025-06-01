package com.example.demo

import com.example.demo.GeocodeApi.GraphHopperResponse
import com.example.demo.GeocodeApi.RequestToApi
import com.example.demo.configs.ApiTokenFeignConfiguration
import com.example.demo.configs.FeignLoggingConfig
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(
    name = "graphopper-service",
    configuration = [
        ApiTokenFeignConfiguration::class,
        FeignLoggingConfig::class,
    ],
)
interface GeocodeApiFeign {
    @PostMapping("/api/1/route")
    fun findRoute(
        @RequestBody request: RequestToApi,
    ): GraphHopperResponse
}
