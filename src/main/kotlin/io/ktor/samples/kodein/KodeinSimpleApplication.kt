package io.ktor.samples.kodein

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton
import java.security.SecureRandom
import java.util.*
import kotlin.random.Random as KRandom

//hola
fun main() {
    embeddedServer(Netty, port = 8080, module = Application::myKodeinApp).start(wait = true)
}

fun Application.myKodeinApp() = myKodeinApp(DI {
    bind<Random>() with singleton { SecureRandom() }
})

fun Application.myKodeinApp(kodein: DI) {
    install(ContentNegotiation) {
        json()
    }

    routing {
        get("/") {
            val random by kodein.instance<Random>()
            val range = 0 until 100
            call.respondText("Random number in $range: ${random[range]}")
        }

        get("/users") {
            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
            val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 10
            val allUsers = generateUsers(100)
            val fromIndex = (page - 1) * size
            val toIndex = (fromIndex + size).coerceAtMost(allUsers.size)

            if (fromIndex >= allUsers.size || fromIndex < 0) {
                call.respond(emptyList<User>())
            } else {
                call.respond(allUsers.subList(fromIndex, toIndex))
            }
        }
    }
}

@Serializable
data class User(val id: Int, val name: String, val city: String)

fun generateUsers(count: Int): List<User> {
    val names = listOf("Alice", "Bob", "Charlie", "Diana", "Eve", "Frank", "Grace", "Hank", "Ivy", "Jack")
    val cities = listOf("New York", "London", "Paris", "Berlin", "Tokyo", "San Francisco", "Madrid", "Rome", "Toronto", "Sydney")
    return (1..count).map {
        User(
            id = it,
            name = names.random() + it,
            city = cities.random()
        )
    }
}

private operator fun Random.get(range: IntRange) = range.first + this.nextInt(range.last - range.first)
