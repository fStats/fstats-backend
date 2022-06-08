package dev.syoritohatsuki.fsats.dto

import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
data class StatusMessage(
    val code: Int = HttpStatusCode.OK.value,
    val message: String = HttpStatusCode.OK.description
)
