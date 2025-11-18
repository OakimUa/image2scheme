package org.oakim.img2scheme.serialization

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.KotlinModule

val JSON: ObjectMapper by lazy {
    ObjectMapper()
        .registerModule(KotlinModule.Builder().build())
        .registerModule(SimpleModule().apply {
            addSerializer(ImageBitmap::class.java, ImageBitmapSerializer())
            addDeserializer(ImageBitmap::class.java, ImageBitmapDeserializer())
        })
        .registerModule(SimpleModule().apply {
            addKeySerializer(Pair::class.java, PairKeySerializer())
            addKeyDeserializer(Pair::class.java, PairKeyDeserializer())
        })
        .registerModule(SimpleModule().apply {
            addSerializer(Offset::class.java, OffsetSerializer())
            addDeserializer(Offset::class.java, OffsetDeserializer())
        })
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
}
