package com.example.demo

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.time.DayOfWeek
import java.util.*
import javax.validation.ConstraintViolationException
import javax.validation.constraints.NotNull

/**
 * @author Julius Krah
 */
@Validated
@RestController
class DemoController {
    @Autowired
    lateinit var service: DemoService

    /**
     * Receives a JSON request of coerces it into a formatted text.
     * Information received is validated.
     *
     * Enum maps are maintained in the <i>natural order</i> of their keys
     */
    @PostMapping("/opening-hours")
    fun openingHours(
            @RequestBody openingHours: EnumMap<DayOfWeek, @NotNull ArrayList<OpenClose>>
    ): Mono<String> {
        return Mono.just(
                service.coerceTextFromJson(openingHours)
        )
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun constraintErrorHandler(ex: ConstraintViolationException): String {
        return "Uh uh! We couldn't validate your input"
    }

    @ExceptionHandler(RuntimeException::class)
    fun runtimeErrorHandler(ex: RuntimeException): String {
        return ex.message!!
    }
}