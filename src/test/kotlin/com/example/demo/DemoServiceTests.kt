package com.example.demo

import com.example.demo.OpenClose.Type.CLOSE
import com.example.demo.OpenClose.Type.OPEN
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.DayOfWeek.*
import java.time.LocalTime.ofSecondOfDay

class DemoServiceTests {

    @Test
    fun `test coercion`() {
        val daysOfWork = mapOf(
                MONDAY to arrayListOf(),
                TUESDAY to arrayListOf(
                        OpenClose(CLOSE, ofSecondOfDay(64800)),
                        OpenClose(OPEN, ofSecondOfDay(36000)),
                ),
                WEDNESDAY to arrayListOf(),
                THURSDAY to arrayListOf(
                        OpenClose(OPEN, ofSecondOfDay(37800)),
                        OpenClose(CLOSE, ofSecondOfDay(64800))
                ),
                FRIDAY to arrayListOf(
                        OpenClose(OPEN, ofSecondOfDay(36000))
                ),
                SATURDAY to arrayListOf(
                        OpenClose(OPEN, ofSecondOfDay(32400)),
                        OpenClose(CLOSE, ofSecondOfDay(3600)),
                        OpenClose(CLOSE, ofSecondOfDay(39600)),
                        OpenClose(OPEN, ofSecondOfDay(57600))
                ),
                SUNDAY to arrayListOf(
                        OpenClose(OPEN, ofSecondOfDay(43200)),
                        OpenClose(CLOSE, ofSecondOfDay(3600)),
                        OpenClose(CLOSE, ofSecondOfDay(75600))
                ),
        )
        val service = DemoService()
        val workingDays = service.coerceTextFromJson(daysOfWork)

        val expected = listOf(
                "Monday: Closed",
                "Tuesday: 10 AM - 6 PM",
                "Wednesday: Closed",
                "Thursday: 10:30 AM - 6 PM",
                "Friday: 10 AM - 1 AM",
                "Saturday: 9 AM - 11 AM, 4 PM - 1 AM",
                "Sunday: 12 PM - 9 PM",
        ).joinToString(System.lineSeparator())

        assertThat(workingDays)
                .isInstanceOf(String::class.java)
                .contains(expected)
    }
}