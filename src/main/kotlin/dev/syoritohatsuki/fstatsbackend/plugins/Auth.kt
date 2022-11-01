package dev.syoritohatsuki.fstatsbackend.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import dev.syoritohatsuki.fstatsbackend.mics.JWT_REALM
import dev.syoritohatsuki.fstatsbackend.mics.JWT_SALT
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureAuth() {
    install(Authentication) {
        jwt {
            realm = JWT_REALM
            verifier(JWT.require(Algorithm.HMAC256(JWT_SALT)).build())
        }
    }
}