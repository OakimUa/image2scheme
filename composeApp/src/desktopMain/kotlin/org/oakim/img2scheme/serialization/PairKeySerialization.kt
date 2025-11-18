package org.oakim.img2scheme.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.KeyDeserializer
import com.fasterxml.jackson.databind.SerializerProvider

class PairKeySerializer : JsonSerializer<Pair<*, *>>() {
    override fun serialize(value: Pair<*, *>, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeFieldName("${value.first},${value.second}")
    }
}

class PairKeyDeserializer : KeyDeserializer() {
    override fun deserializeKey(key: String, ctxt: DeserializationContext): Any {
        val parts = key.split(",")
        return Pair(parts[0].toInt(), parts[1].toInt())
    }
}