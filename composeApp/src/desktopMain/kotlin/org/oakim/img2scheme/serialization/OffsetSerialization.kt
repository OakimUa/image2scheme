package org.oakim.img2scheme.serialization

import androidx.compose.ui.geometry.Offset
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider

class OffsetSerializer : JsonSerializer<Offset>() {
    override fun serialize(value: Offset, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeStartObject()
        gen.writeNumberField("x", value.x)
        gen.writeNumberField("y", value.y)
        gen.writeEndObject()
    }
}

class OffsetDeserializer : JsonDeserializer<Offset?>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Offset {
        val node = p.codec.readTree<JsonNode>(p)
        val x = node["x"].asDouble().toFloat()
        val y = node["y"].asDouble().toFloat()
        return Offset(x, y)
    }
}