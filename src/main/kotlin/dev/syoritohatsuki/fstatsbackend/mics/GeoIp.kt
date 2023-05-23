package dev.syoritohatsuki.fstatsbackend.mics

import dev.syoritohatsuki.fstatsbackend.plugins.json
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import java.net.URL

fun String.getGeolocationByIp(): String = runCatching<String> {
    json.decodeFromString<JsonElement>(URL("http://ip-api.com/json/$this?fields=status,country").readText()).jsonObject["country"].toString()
}.getOrNull() ?: "unknown"