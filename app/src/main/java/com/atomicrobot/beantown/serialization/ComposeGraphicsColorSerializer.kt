package com.atomicrobot.beantown.serialization

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object ComposeGraphicsColorSerializer : KSerializer<Color> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("androidx.compose.ui.graphics.Color", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Color): Unit =
        encoder.encodeString(value.toHexString())

    override fun deserialize(decoder: Decoder): Color =
        decoder.decodeString().lowercase().let {
            runCatching {
                if(it == "#") Color.Unspecified
                else Color(it.toColorInt())
            }.getOrNull() ?: throw SerializationException("Unknown color: $it")
        }
}

private fun Color.toHexString(): String = "#${(value shr 32).toString(16).uppercase()}"