package com.example.demo

import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.anyMap
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.web.reactive.function.BodyInserters
import java.time.DayOfWeek
import java.time.DayOfWeek.*
import java.util.*

@WebFluxTest(controllers = [DemoController::class])
class DemoControllerTests {
    @Autowired
    private lateinit var client: WebTestClient

    @MockBean
    private lateinit var service: DemoService

    @Test
    fun `test opening hours`() {
        val mockResponse =
                """
                    Monday: Closed
                    Tuesday: 10 AM - 6 PM
                    Wednesday: Closed
                    Thursday: 10:30 AM - 6 PM
                    Friday: 10 AM - 1 AM
                    Saturday: 10 AM - 1 AM
                    Sunday: 12 PM - 9 PM
                """.trimIndent()
        val openingHours = HashMap<DayOfWeek, List<OpenClose>>()
        openingHours[MONDAY] = listOf()
        openingHours[TUESDAY] = listOf()
        openingHours[WEDNESDAY] = listOf()
        openingHours[THURSDAY] = listOf()
        openingHours[FRIDAY] = listOf()
        openingHours[SATURDAY] = listOf()
        openingHours[SUNDAY] = listOf()

        given(service.coerceTextFromJson(anyMap())).willReturn(mockResponse)
        client.post()
                .uri("/opening-hours")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(openingHours))
                .exchange()
                .expectStatus().isOk
                .expectBody<String>().isEqualTo(mockResponse)
    }
}