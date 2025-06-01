package com.example.demo

import com.example.demo.GeocodeApi.GraphHopperResponse
import com.example.demo.GeocodeApi.RequestToApi
import com.example.demo.configs.GraphhopperApiConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.support.RestTemplateAdapter
import org.springframework.web.service.annotation.PostExchange
import org.springframework.web.service.invoker.HttpRequestValues
import org.springframework.web.service.invoker.HttpServiceProxyFactory
import org.springframework.web.service.invoker.UrlArgumentResolver
import org.springframework.web.util.DefaultUriBuilderFactory

interface RestSpringInterfaceGraphhopper {
    @PostExchange(value = "/api/1/route", contentType = "application/json", accept = [MediaType.APPLICATION_JSON_VALUE])
    fun findRoute(
        @RequestBody request: RequestToApi,
    ): GraphHopperResponse
}

class CustomArgumentResolver(
    private val apiKey: String,
) : UrlArgumentResolver() {
    override fun resolve(
        argument: Any?,
        parameter: MethodParameter,
        requestValues: HttpRequestValues.Builder,
    ): Boolean {
        var url = requestValues.uriTemplate

        url += "?key=$apiKey"

        requestValues.setUriTemplate(url!!)
        requestValues.build()

        return true
    }
}

@Configuration
class GraphhoperRestSpringInterfaceConfig {
    @Bean
    fun RestSpringInterfaceGraphhopper(
        restTemplate: RestTemplate,
        graphhopperApiConfig: GraphhopperApiConfig,
    ): RestSpringInterfaceGraphhopper {
        restTemplate.uriTemplateHandler = DefaultUriBuilderFactory(graphhopperApiConfig.url)
        val adapter = RestTemplateAdapter.create(restTemplate)
        val factory =
            HttpServiceProxyFactory
                .builderFor(adapter)
                .customArgumentResolver(
                    CustomArgumentResolver(
                        apiKey = graphhopperApiConfig.token,
                    ),
                ).build()

        val service: RestSpringInterfaceGraphhopper =
            factory.createClient<RestSpringInterfaceGraphhopper>(RestSpringInterfaceGraphhopper::class.java)

        return service
    }
}
