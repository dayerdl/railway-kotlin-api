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
                call.respond(UsuarioResponse(usuarios = pagedUsers))
            }
        }
    }.start(wait = true)
}

@Serializable
data class Usuario(
    val id: Int,
    val nombre: String,
    val ciudad: String,
    val imagen: String
)

@Serializable
data class UsuarioResponse(
    val usuarios: List<Usuario>
)

// 100 nombres únicos
val nombresUnicos = List(100) { index -> "Usuario$index" }

val ciudades = listOf(
    "Madrid", "Barcelona", "Sevilla", "Valencia", "Zaragoza",
    "Bilbao", "Granada", "Toledo", "Málaga", "Santander"
)

// Generar 100 usuarios únicos con imágenes de randomuser.me
val usuarios: List<Usuario> = nombresUnicos.mapIndexed { index, nombre ->
    val genero = if (index % 2 == 0) "men" else "women"
    val imagenUrl = "https://randomuser.me/api/portraits/$genero/${index % 100}.jpg"

    Usuario(
        id = index + 1,
        nombre = nombre,
        ciudad = ciudades.random(),
        imagen = imagenUrl
    )
}
