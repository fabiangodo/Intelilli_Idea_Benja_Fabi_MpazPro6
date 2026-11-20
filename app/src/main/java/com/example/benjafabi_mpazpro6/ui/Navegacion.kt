package com.example.benjafabi_mpazpro6.ui

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.benjafabi_mpazpro6.modelo.Rol
import com.example.benjafabi_mpazpro6.ui.docente.PanelDocenteScreen
import com.example.benjafabi_mpazpro6.ui.estudiante.PanelEstudianteScreen
import com.example.benjafabi_mpazpro6.ui.login.LoginScreen
import com.example.benjafabi_mpazpro6.ui.utp.PanelUtpScreen

@Composable
fun AppNavigation() {
    val nav = rememberNavController()

    val cerrarSesion: () -> Unit = {
        nav.navigate("login") {
            popUpTo(nav.graph.id) { inclusive = true }
        }
    }

    NavHost(
        navController = nav,
        startDestination = "login",
        enterTransition = { fadeIn(tween(300)) + slideInHorizontally(tween(300)) { it / 10 } },
        exitTransition = { fadeOut(tween(200)) },
        popEnterTransition = { fadeIn(tween(300)) },
        popExitTransition = { fadeOut(tween(200)) }
    ) {
        composable("login") {
            LoginScreen(onLoginExitoso = { usuario ->
                val destino = when (usuario.rol) {
                    Rol.ESTUDIANTE -> "estudiante/${usuario.id}"
                    Rol.DOCENTE -> "docente/${usuario.id}"
                    Rol.UTP -> "utp/${usuario.id}"
                }
                nav.navigate(destino) { popUpTo("login") { inclusive = true } }
            })
        }

        composable("estudiante/{id}") { entrada ->
            PanelEstudianteScreen(
                usuarioId = entrada.arguments?.getString("id").orEmpty(),
                onCerrarSesion = cerrarSesion
            )
        }

        composable("docente/{id}") { entrada ->
            PanelDocenteScreen(
                usuarioId = entrada.arguments?.getString("id").orEmpty(),
                onCerrarSesion = cerrarSesion
            )
        }

        composable("utp/{id}") { entrada ->
            PanelUtpScreen(
                usuarioId = entrada.arguments?.getString("id").orEmpty(),
                onCerrarSesion = cerrarSesion
            )
        }
    }
}