package com.example.benjafabi_mpazpro6.validacion

import com.example.benjafabi_mpazpro6.modelo.Rol
import com.example.benjafabi_mpazpro6.modelo.Usuario

sealed class ResultadoLogin {
    data class Exito(val usuario: Usuario) : ResultadoLogin()
    data object UsuarioNoEncontrado : ResultadoLogin()
    data object ClaveIncorrecta : ResultadoLogin()
    data class RolIncorrecto(val rolSeleccionado: Rol) : ResultadoLogin()
}

object Autenticador {
    fun evaluar(usuario: Usuario?, clave: String, rolSeleccionado: Rol): ResultadoLogin = when {
        usuario == null -> ResultadoLogin.UsuarioNoEncontrado
        usuario.clave != clave -> ResultadoLogin.ClaveIncorrecta
        usuario.rol != rolSeleccionado -> ResultadoLogin.RolIncorrecto(rolSeleccionado)
        else -> ResultadoLogin.Exito(usuario)
    }
}