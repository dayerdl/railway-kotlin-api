package io.ktor.samples.kodein


import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlin.random.Random

fun main() {
    embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) {
            json()
        }
        routing {
            get("/usuarios") {
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 10

                val pagedUsers = usuarios.chunked(size).getOrNull(page - 1) ?: emptyList()
                call.respond(pagedUsers)
            }
        }
    }.start(wait = true)
}

@Serializable
data class Usuario(val id: Int, val nombre: String, val ciudad: String)

// 100 nombres únicos (puedes agregar más si lo deseas)
val nombresUnicos = List(100) { index -> "Usuario$index" }

val ciudades = listOf(
    "Madrid", "Barcelona", "Sevilla", "Valencia", "Zaragoza",
    "Bilbao", "Granada", "Toledo", "Málaga", "Santander"
)

// Generar 100 usuarios únicos con nombres diferentes
val usuarios: List<Usuario> = nombresUnicos.mapIndexed { index, nombre ->
    Usuario(
        id = index + 1,
        nombre = nombre,
        ciudad = ciudades.random()
    )
}
