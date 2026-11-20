package com.example.benjafabi_mpazpro6

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.benjafabi_mpazpro6.modelo.AppDatabase
import com.example.benjafabi_mpazpro6.ui.AppNavigation
import com.example.benjafabi_mpazpro6.ui.theme.LocalTemaControl
import com.example.benjafabi_mpazpro6.ui.theme.ModoTema
import com.example.benjafabi_mpazpro6.ui.theme.MpazProTheme
import com.example.benjafabi_mpazpro6.ui.theme.TemaControl

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Fuerza la creación de la base (y la precarga de usuarios)
        AppDatabase.getDatabase(applicationContext).openHelper.writableDatabase

        val prefs = getSharedPreferences("ajustes_mpaz", MODE_PRIVATE)

        setContent {
            var modo by remember {
                mutableStateOf(ModoTema.desde(prefs.getString("modo_tema", null)))
            }
            val oscuro = when (modo) {
                ModoTema.SISTEMA -> isSystemInDarkTheme()
                ModoTema.CLARO -> false
                ModoTema.OSCURO -> true
            }

            // Mantiene legibles los íconos de la barra de estado según el tema elegido
            DisposableEffect(oscuro) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { oscuro },
                    navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { oscuro }
                )
                onDispose { }
            }

            val control = TemaControl(modo) { nuevo ->
                modo = nuevo
                prefs.edit().putString("modo_tema", nuevo.name).apply()
            }

            CompositionLocalProvider(LocalTemaControl provides control) {
                MpazProTheme(darkTheme = oscuro) {
                    AppNavigation()
                }
            }
        }
    }
}