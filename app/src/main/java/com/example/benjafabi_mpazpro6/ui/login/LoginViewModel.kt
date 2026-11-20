package com.example.benjafabi_mpazpro6.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.benjafabi_mpazpro6.modelo.AppDatabase
import com.example.benjafabi_mpazpro6.modelo.Rol
import com.example.benjafabi_mpazpro6.modelo.Usuario
import com.example.benjafabi_mpazpro6.modelo.poblarBaseInicial
import com.example.benjafabi_mpazpro6.validacion.Autenticador
import com.example.benjafabi_mpazpro6.validacion.ResultadoLogin
import com.example.benjafabi_mpazpro6.validacion.ValidadorClave
import com.example.benjafabi_mpazpro6.validacion.ValidadorNombreUsuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val nombreUsuario: String = "",
    val clave: String = "",
    val rolSeleccionado: Rol = Rol.ESTUDIANTE,
    val errorUsuario: String? = null,
    val errorClave: String? = null,
    val errorGeneral: String? = null,
    val cargando: Boolean = false,
    val usuarioLogueado: Usuario? = null
)

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val dao = db.usuarioDao()

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onNombreUsuarioChange(valor: String) {
        _uiState.update {
            it.copy(
                nombreUsuario = valor,
                errorUsuario = if (it.errorUsuario != null)
                    ValidadorNombreUsuario.validar(valor.trim()).mensajeError else null,
                errorGeneral = null
            )
        }
    }

    fun onClaveChange(valor: String) {
        _uiState.update {
            it.copy(
                clave = valor,
                errorClave = if (it.errorClave != null)
                    ValidadorClave.validar(valor).mensajeError else null,
                errorGeneral = null
            )
        }
    }

    fun onRolChange(rol: Rol) {
        _uiState.update { it.copy(rolSeleccionado = rol, errorGeneral = null) }
    }

    fun iniciarSesion() {
        val actual = _uiState.value
        if (actual.cargando) return

        val nombre = actual.nombreUsuario.trim()
        val errorUsuario = ValidadorNombreUsuario.validar(nombre).mensajeError
        val errorClave = ValidadorClave.validar(actual.clave).mensajeError

        if (errorUsuario != null || errorClave != null) {
            _uiState.update {
                it.copy(errorUsuario = errorUsuario, errorClave = errorClave, errorGeneral = null)
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(cargando = true, errorUsuario = null, errorClave = null, errorGeneral = null)
            }

            // 1. Busca el usuario en la base de datos
            var encontrado = dao.obtenerPorNombreUsuario(nombre)

            // 2. Si no se encuentra (porque el callback 'onCreate' de Room no se ejecutó),
            // fuerza la inyección de los datos iniciales y re-consulta inmediatamente.
            if (encontrado == null) {
                poblarBaseInicial(db)
                encontrado = dao.obtenerPorNombreUsuario(nombre)
            }

            val resultado = Autenticador.evaluar(encontrado, actual.clave, actual.rolSeleccionado)

            _uiState.update { estado ->
                when (resultado) {
                    is ResultadoLogin.Exito ->
                        estado.copy(cargando = false, usuarioLogueado = resultado.usuario)
                    ResultadoLogin.UsuarioNoEncontrado ->
                        estado.copy(cargando = false, errorUsuario = "No encontramos ese usuario")
                    ResultadoLogin.ClaveIncorrecta ->
                        estado.copy(cargando = false, errorClave = "La contraseña no es correcta")
                    is ResultadoLogin.RolIncorrecto ->
                        estado.copy(
                            cargando = false,
                            errorGeneral = "Este usuario no pertenece al perfil " +
                                    "${resultado.rolSeleccionado.mostrarNombre}. Cambia de perfil arriba."
                        )
                }
            }
        }
    }
}