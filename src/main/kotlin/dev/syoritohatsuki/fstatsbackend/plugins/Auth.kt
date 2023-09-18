package dev.syoritohatsuki.fstatsbackend.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import dev.syoritohatsuki.fstatsbackend.mics.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureAuth() {
    install(Authentication) {
        jwt {
            realm = JWT_REALM
            verifier(
                JWT.require(Algorithm.HMAC256(JWT_SECRET))
                    .withIssuer("$HOST:$PORT")
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim("username").asString() != "") JWTPrincipal(credential.payload) else null
            }
            challenge { _, _ ->
                call.respondMessage(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
        }
    }
}