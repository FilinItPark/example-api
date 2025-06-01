package com.example.demo.configs

import feign.Logger
import feign.Request
import feign.RequestInterceptor
import feign.RequestTemplate
import org.slf4j.LoggerFactory
import org.springframework.cloud.openfeign.DefaultFeignLoggerFactory
import org.springframework.cloud.openfeign.FeignLoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ApiTokenFeignConfiguration {
    @Bean
    fun apiKeyInterceptor(graphhopperApiConfig: GraphhopperApiConfig) =
        ApiKeyInterceptor(
            apiKey = graphhopperApiConfig.token,
        )
}

@Configuration
class FeignLoggingConfig {
    @Bean
    fun loggerFactory(): FeignLoggerFactory = DefaultFeignLoggerFactory(CustomLogger())
}

class CustomLogger : Logger() {
    val logger = LoggerFactory.getLogger(CustomLogger::class.java)

    override fun log(
        p0: String?,
        p1: String?,
        vararg p2: Any?,
    ) {
    }

    private fun getRequestId(request: Request?): String = request.hashCode().toString()

    override fun logRequest(
        configKey: String?,
        logLevel: Level?,
        request: Request?,
    ) {
        val messageBuilder = StringBuilder()

        messageBuilder.append("[REQ] ")
        messageBuilder.append(
            "[$logLevel] ",
        )
        messageBuilder.append(getRequestId(request))
        messageBuilder.append(": ")
        messageBuilder.append(request?.httpMethod())
        messageBuilder.append(" ")
        messageBuilder.append(request?.url())
        messageBuilder.append("\n FEIGN ")
        messageBuilder.append(configKey)
        messageBuilder.append(" \n")

        val bodyText = String(request!!.body(), request.charset())
        messageBuilder.append(bodyText)

        val log = messageBuilder.toString()

        logger.info(log)
    }
}

class ApiKeyInterceptor(
    private val apiKey: String,
) : RequestInterceptor {
    override fun apply(template: RequestTemplate?) {
        var url = template?.url()

        url += "?key=$apiKey"

        template?.uri(url)
    }
}
