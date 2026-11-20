package com.example.benjafabi_mpazpro6.ui.theme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class MpazColores(
    val fondo: Color,
    val superficie: Color,
    val texto: Color,
    val textoSecundario: Color,
    val titulo: Color,          // títulos y navegación
    val marca: Color,           // botones principales y elementos destacados
    val sobreMarca: Color,      // texto o ícono que va SOBRE `marca`
    val marcaSuave: Color,      // fondos y tarjetas tenues
    val marcaProfunda: Color,   // estados activos
    val info: Color,
    val progreso: Color,
    val logro: Color,
    val correcto: Color,
    val incorrecto: Color,
    val advertencia: Color,
    val pendiente: Color,
    val borde: Color
)

private val ColoresClaros = MpazColores(
    fondo = ClaroFondo,
    superficie = ClaroSuperficie,
    texto = ClaroTexto,
    textoSecundario = ClaroTextoSecundario,
    titulo = AzulInstitucional,
    marca = RosadoPasion,
    sobreMarca = ClaroTexto,
    marcaSuave = RosadoSuave,
    marcaProfunda = RosadoProfundo,
    info = AzulSecundario,
    progreso = Turquesa,
    logro = AmarilloLogro,
    correcto = Correcto,
    incorrecto = Incorrecto,
    advertencia = Advertencia,
    pendiente = PreguntaPendiente,
    borde = Color(0xFFE4E7EC)
)

private val ColoresOscuros = MpazColores(
    fondo = OscuroFondo,
    superficie = OscuroSuperficie,
    texto = OscuroTexto,
    textoSecundario = OscuroTextoSecundario,
    titulo = OscuroTexto,       // el azul institucional no se ve sobre fondo oscuro
    marca = RosadoPasion,
    sobreMarca = ClaroTexto,
    marcaSuave = Color(0x33F575BD),
    marcaProfunda = RosadoProfundo,
    info = AzulSecundario,
    progreso = Turquesa,
    logro = AmarilloLogro,
    correcto = Correcto,
    incorrecto = Incorrecto,
    advertencia = Advertencia,
    pendiente = PreguntaPendiente,
    borde = Color(0xFF3A4458)
)

val LocalMpazColores = staticCompositionLocalOf { ColoresClaros }

object Mpaz {
    val colores: MpazColores
        @Composable @ReadOnlyComposable get() = LocalMpazColores.current
}

@Composable
fun MpazProTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val c = if (darkTheme) ColoresOscuros else ColoresClaros

    val esquema = if (darkTheme) {
        darkColorScheme(
            primary = c.marca, onPrimary = c.sobreMarca,
            secondary = AzulSecundario, tertiary = Turquesa,
            background = c.fondo, onBackground = c.texto,
            surface = c.superficie, onSurface = c.texto,
            onSurfaceVariant = c.textoSecundario,
            outline = c.borde, error = Incorrecto
        )
    } else {
        lightColorScheme(
            primary = c.marca, onPrimary = c.sobreMarca,
            secondary = AzulSecundario, tertiary = Turquesa,
            background = c.fondo, onBackground = c.texto,
            surface = c.superficie, onSurface = c.texto,
            onSurfaceVariant = c.textoSecundario,
            outline = c.borde, error = Incorrecto
        )
    }

    CompositionLocalProvider(LocalMpazColores provides c) {
        MaterialTheme(
            colorScheme = esquema,
            typography = Typography,
            content = content
        )
    }
}