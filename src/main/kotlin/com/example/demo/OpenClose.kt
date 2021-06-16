package com.example.demo

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.ObjectCodec
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import java.time.LocalTime
import javax.validation.constraints.NotNull

/**
 * Representation of Opening and Closing time
 * @author Julius Krah
 */
data class OpenClose(
        val type: Type,
        @get:NotNull
        @JsonDeserialize(using = LocalTimeDeserializer::class)
        val value: LocalTime
) : Comparable<OpenClose> {

    /**
     * Naturally we would want to sort the opening hours in a day by time opened
     */
    override fun compareTo(other: OpenClose): Int {
        return this.value.compareTo(other.value)
    }

    /**
     * Either Open or Closed
     */
    enum class Type {
        OPEN, CLOSE
    }

    /**
     * Custom deserializer that converts from Unix Time to LocalTime
     */
    class LocalTimeDeserializer : JsonDeserializer<LocalTime>() {
        override fun deserialize(jp: JsonParser, dc: DeserializationContext?): LocalTime {
            val oc: ObjectCodec = jp.codec
            val node: JsonNode = oc.readTree(jp)
            // In a production ready application, we'd do a null check here
            return LocalTime.ofSecondOfDay(node.asLong())
        }
    }

}