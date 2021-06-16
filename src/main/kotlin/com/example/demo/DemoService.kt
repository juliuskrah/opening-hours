package com.example.demo

import com.example.demo.OpenClose.Type.CLOSE
import com.example.demo.OpenClose.Type.OPEN
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service
import java.time.DayOfWeek
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle.FULL

/**
 * @author Julius Krah
 */
@Service
class DemoService {
    val log: Logger = LoggerFactory.getLogger(DemoService::class.java)

    /**
     * Creates a human readable String of opening hours for a restaurant
     */
    fun coerceTextFromJson(
            hoursOfWork: Map<DayOfWeek, ArrayList<OpenClose>>
    ): String {
        val local = LocaleContextHolder.getLocale()
        log.debug("This is what we have to work with {}", hoursOfWork)
        val days = mutableListOf<String>()
        for ((day, hours) in hoursOfWork) {
            hours.sort() // Sort list by LocalTime
            val dayString = day.getDisplayName(FULL, local)
            val workingHour = if (hours.isEmpty()) "Closed" else computeWorkingHours(day, hours, hoursOfWork)
            val representation = "$dayString: $workingHour"
            days.add(representation)
        }
        return days.joinToString(System.lineSeparator())
    }

    private fun computeWorkingHours(
            today: DayOfWeek, hours: ArrayList<OpenClose>,
            operatingHours: Map<DayOfWeek, ArrayList<OpenClose>>
    ): String {
        // If first element is a closing time, then it spilled over from previous day
        if (hours.first().type == CLOSE) hours.removeFirst()
        // If last element is a opening time, then find closing time from next day
        if (hours.last().type == OPEN) {
            val tomorrow = today.plus(1)
            val tomorrowHours = operatingHours[tomorrow]
            if (tomorrowHours.isNullOrEmpty()) throw RuntimeException("Valid operating hours must be provided for $tomorrow")
            tomorrowHours.sort()
            hours.add(tomorrowHours.first())
        }
        val openClose = hours.withIndex().zipWithNext { a, b ->
            if (a.index % 2 == 0) {
                val formatter = DateTimeFormatter.ofPattern("h[:mm] a")
                "${a.value.value.format(formatter).replace(":00", "")} - ${b.value.value.format(formatter).replace(":00", "")}"
            } else ""
        }.filter { it.isNotBlank() }
        return openClose.joinToString(", ")
    }
}