package com.example.benjafabi_mpazpro6.validacion

sealed class ResultadoValidacion {
    data object Valido : ResultadoValidacion()
    data class Invalido(val mensaje: String) : ResultadoValidacion()

    val mensajeError: String?
        get() = (this as? Invalido)?.mensaje
}

interface Validador<T> {
    fun validar(valor: T): ResultadoValidacion
}

object ValidadorNombreUsuario : Validador<String> {
    override fun validar(valor: String): ResultadoValidacion = when {
        valor.isBlank() -> ResultadoValidacion.Invalido("Ingresa tu nombre de usuario")
        valor.length < 3 -> ResultadoValidacion.Invalido("Debe tener al menos 3 caracteres")
        valor.any { it.isWhitespace() } -> ResultadoValidacion.Invalido("No puede contener espacios")
        else -> ResultadoValidacion.Valido
    }
}

object ValidadorClave : Validador<String> {
    override fun validar(valor: String): ResultadoValidacion = when {
        valor.isEmpty() -> ResultadoValidacion.Invalido("Ingresa tu contraseña")
        valor.length < 6 -> ResultadoValidacion.Invalido("Debe tener al menos 6 caracteres")
        else -> ResultadoValidacion.Valido
    }
}