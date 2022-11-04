package dev.syoritohatsuki.fstatsbackend.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import dev.syoritohatsuki.fstatsbackend.mics.HOST
import dev.syoritohatsuki.fstatsbackend.mics.JWT_REALM
import dev.syoritohatsuki.fstatsbackend.mics.JWT_SECRET
import dev.syoritohatsuki.fstatsbackend.mics.PORT
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureAuth() {
    install(Authentication) {
        jwt {
            realm = JWT_REALM
            verifier(
                JWT.require(Algorithm.HMAC256(JWT_SECRET))
                    .withAudience("users")
                    .withIssuer("$HOST:$PORT")
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains("users")) JWTPrincipal(credential.payload) else null
            }
        }
    }
}