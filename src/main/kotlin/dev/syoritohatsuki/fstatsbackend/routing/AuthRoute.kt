package dev.syoritohatsuki.fstatsbackend.routing

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import de.nycode.bcrypt.hash
import de.nycode.bcrypt.verify
import dev.syoritohatsuki.fstatsbackend.dao.impl.UserDAOImpl
import dev.syoritohatsuki.fstatsbackend.dto.User
import dev.syoritohatsuki.fstatsbackend.mics.HOST
import dev.syoritohatsuki.fstatsbackend.mics.JWT_SECRET
import dev.syoritohatsuki.fstatsbackend.mics.PORT
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRoute() {
    route("auth") {
        post("login") {
            val user = call.receive<User>()

            if (user.username.isEmpty() || user.password.isEmpty()) {
                println("${HttpStatusCode.BadRequest} Incorrect username or password")
                return@post call.respond(HttpStatusCode.BadRequest, "Incorrect username or password")
            }

            UserDAOImpl.getByName(user.username).let {
                if (it == null) {
                    println("${HttpStatusCode.BadRequest} Incorrect username or password")
                    return@post call.respond(HttpStatusCode.BadRequest, "Incorrect username or password")
                }

                if (!verify(user.password, it.passwordHash.toByteArray())) {
                    println("${HttpStatusCode.BadRequest} Incorrect username or password")
                    return@post call.respond(HttpStatusCode.BadRequest, "Incorrect username or password")
                }

                call.respond(
                    hashMapOf(
                        "token" to JWT.create()
                            .withAudience("user")
                            .withClaim("id", it.id)
                            .withClaim("username", it.username)
                            .withIssuer("$HOST:$PORT")
                            .sign(Algorithm.HMAC256(JWT_SECRET))
                    )
                )
            }
        }
        post("registration") {
            call.receive<User>().let { user ->
                if (Regex("^([a-zA-Z0-9_]).{3,16}\$").matches(user.username)
                    && Regex("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?!.*\\s).{8,64}\$").matches(user.password)
                ) {
                    UserDAOImpl.create(User(username = user.username, passwordHash = String(hash(user.password))))
                        .let {
                            if (it.second == 1) {
                                call.respond(HttpStatusCode.Created, "User created")
                                println("${HttpStatusCode.Created} User created")
                            } else {
                                call.respond(HttpStatusCode.BadRequest, "Username already exist")
                                println("${HttpStatusCode.BadRequest} Username already exist")
                            }
                        }
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Username or password not match requirements")
                    println("${HttpStatusCode.BadRequest} Username or password not match requirements")
                }
            }
        }
    }
}