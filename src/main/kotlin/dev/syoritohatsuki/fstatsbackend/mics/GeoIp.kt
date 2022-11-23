package dev.syoritohatsuki.fstatsbackend.mics

import dev.syoritohatsuki.fstatsbackend.plugins.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import java.net.URL

fun String.getGeolocationByIp(): GeoIp? = runCatching<GeoIp> {
    json.decodeFromString(URL("http://ip-api.com/json/$this?fields=status,country").readText())
}.getOrNull()

@Serializable
data class GeoIp(val status: String, val country: String)