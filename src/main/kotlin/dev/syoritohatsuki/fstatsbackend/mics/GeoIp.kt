package dev.syoritohatsuki.fstatsbackend.mics

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

suspend fun getGeolocationByIp(ip: String): GeoIp = HttpClient().get {
    url("http://ip-api.com/json/$ip?fields=status,country")
}.body()

data class GeoIp(
    val status: String,
    val country: String,
)