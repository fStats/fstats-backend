package dev.syoritohatsuki.fstatsbackend.mics

import dev.syoritohatsuki.fstatsbackend.plugins.json
import kotlinx.serialization.decodeFromString
import java.net.URL

fun String.getGeolocationByIp(): GeoIp? = runCatching {
    return json.decodeFromString(URL("http://ip-api.com/json/$this?fields=status,country").readText())
}.getOrNull()

data class GeoIp(
    val status: String,
    val country: String,
)