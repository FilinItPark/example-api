package com.example.demo

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Component

@Component
@ConditionalOnBean(GeocodeApi::class)
class CreateRequestToApi(
    private val geocodeApi: GeocodeApi,
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        geocodeApi.sendRequestToAPI(
            from =
                Pair(
                    11.539421,
                    48.118477,
                ),
            to =
                Pair(
                    11.559023,
                    48.12228,
                ),
        )
    }
}
