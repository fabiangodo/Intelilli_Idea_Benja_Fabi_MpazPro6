package com.example.benjafabi_mpazpro6.ui.theme

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class ModoTema(val etiqueta: String, val icono: ImageVector) {
    SISTEMA("Automático (sistema)", Icons.Default.SettingsBrightness),
    CLARO("Claro", Icons.Default.LightMode),
    OSCURO("Oscuro", Icons.Default.DarkMode);

    companion object {
        fun desde(nombre: String?): ModoTema =
            entries.firstOrNull { it.name == nombre } ?: SISTEMA
    }
}

class TemaControl(val modo: ModoTema, val cambiar: (ModoTema) -> Unit)

val LocalTemaControl = staticCompositionLocalOf { TemaControl(ModoTema.SISTEMA) {} }

@Composable
fun SelectorTema(
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    val control = LocalTemaControl.current
    var abierto by remember { mutableStateOf(false) }

    Box(modifier) {
        IconButton(onClick = { abierto = true }) {
            Icon(control.modo.icono, contentDescription = "Cambiar tema", tint = tint)
        }
        DropdownMenu(expanded = abierto, onDismissRequest = { abierto = false }) {
            ModoTema.entries.forEach { modo ->
                DropdownMenuItem(
                    text = { Text(modo.etiqueta) },
                    leadingIcon = { Icon(modo.icono, contentDescription = null) },
                    trailingIcon = {
                        if (modo == control.modo) {
                            Icon(Icons.Default.Check, contentDescription = "Seleccionado")
                        }
                    },
                    onClick = {
                        control.cambiar(modo)
                        abierto = false
                    }
                )
            }
        }
    }
}