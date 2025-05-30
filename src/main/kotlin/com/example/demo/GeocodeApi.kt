package com.example.demo

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import org.springframework.http.HttpEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class GeocodeApi(
    private val restTemplate: RestTemplate,
) {
    private var API_URL = "https://graphhopper.com"
    private var ROUTE_URL = "/api/1/route?key="
    private val API_TOKEN = "0028f46d-7d01-4395-8097-974f036d5a32"

    @JsonNaming(
        PropertyNamingStrategy.SnakeCaseStrategy::class,
    )
    @JsonInclude(JsonInclude.Include.NON_NULL)
    data class RequestToApi(
        val points: List<List<Double>>,
        val snapPreventions: List<String>,
        val details: List<String>,
        val profile: String,
        val locale: String = "ru",
        val instructions: Boolean = true,
        val calcPoints: Boolean = true,
        val pointsEncoded: Boolean = false,
    )

    data class GraphHopperResponse(
        val hints: Hints,
        val info: Info,
        val paths: List<Path>,
    ) {
        /** Обёртка для полей hint'ов с точечными именами */
        data class Hints(
            @JsonProperty("visited_nodes.sum")
            val visitedNodesSum: Int,
            @JsonProperty("visited_nodes.average")
            val visitedNodesAverage: Int,
        )

        @JsonNaming(
            PropertyNamingStrategy.SnakeCaseStrategy::class,
        )
        data class Info(
            val copyrights: List<String>,
            val took: Int,
            val roadDataTimestamp: String,
        )

        @JsonNaming(
            PropertyNamingStrategy.SnakeCaseStrategy::class,
        )
        data class Path(
            val distance: Double,
            val weight: Double,
            /** время в миллисекундах */
            val time: Long,
            val transfers: Int,
            val pointsEncoded: Boolean,
            val bbox: List<Double>,
            val points: Points,
            val instructions: List<Instruction>,
            val legs: List<Any>, // пока без детальной модели legs, так как массив пустой
            val details: Details,
            val ascend: Double,
            val descend: Double,
            val snappedWaypoints: SnappedWaypoints,
        ) {
            /** Геометрия маршрута в GeoJSON-стиле */
            data class Points(
                val type: String,
                val coordinates: List<List<Double>>,
            )

            @JsonNaming(
                PropertyNamingStrategy.SnakeCaseStrategy::class,
            )
            data class Instruction(
                val distance: Double,
                val heading: Double? = null,
                val sign: Int,
                val interval: List<Int>,
                val text: String,
                val time: Long,
                val streetName: String,
                val lastHeading: Double? = null,
            )

            @JsonNaming(
                PropertyNamingStrategy.SnakeCaseStrategy::class,
            )
            data class Details(
                val surface: List<List<Any>>,
                val roadClass: List<List<Any>>,
            )

            /** Упрощённое представление «заломанных» точек маршрута */
            data class SnappedWaypoints(
                val type: String,
                val coordinates: List<List<Double>>,
            )
        }
    }

    fun sendRequestToAPI(
        from: Pair<Double, Double>,
        to: Pair<Double, Double>,
    ) {
        val request =
            HttpEntity<RequestToApi>(
                RequestToApi(
                    points =
                        listOf(
                            listOf(from.first, from.second),
                            listOf(to.first, to.second),
                        ),
                    snapPreventions =
                        listOf(
                            "motorway",
                            "ferry",
                            "tunnel",
                        ),
                    details =
                        listOf(
                            "road_class",
                            "surface",
                        ),
                    profile = "car",
                ),
            )
        val response =
            restTemplate
                .postForObject("$API_URL$ROUTE_URL$API_TOKEN", request, GraphHopperResponse::class.java)

        println(response?.paths?.get(0)?.time!! / (60 * 60 * 60))
    }
}
