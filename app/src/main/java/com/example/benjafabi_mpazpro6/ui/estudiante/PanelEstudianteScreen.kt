package com.example.benjafabi_mpazpro6.ui.estudiante

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.benjafabi_mpazpro6.ui.theme.Mpaz
import com.example.benjafabi_mpazpro6.ui.theme.SelectorTema

/** Dónde está el estudiante dentro de su panel. */
sealed class SeccionEstudiante {
    object Inicio : SeccionEstudiante()
    object Asignaturas : SeccionEstudiante()
    object Progreso : SeccionEstudiante()
    object Desafios : SeccionEstudiante()
    data class Unidad(val oaCodigo: String) : SeccionEstudiante()
    data class Lectura(val oaCodigo: String) : SeccionEstudiante()
    data class Cuestionario(val oaCodigo: String, val modo: ModoCuestionario) : SeccionEstudiante()
}

@Composable
fun PanelEstudianteScreen(
    usuarioId: String,
    onCerrarSesion: () -> Unit,
    viewModel: PanelEstudianteViewModel = viewModel(),
    cuestionarioVm: CuestionarioViewModel = viewModel()
) {
    val c = Mpaz.colores
    val estado by viewModel.uiState.collectAsState()
    val cuestionario by cuestionarioVm.estado.collectAsState()

    LaunchedEffect(usuarioId) { viewModel.cargar(usuarioId) }

    var seccion by remember { mutableStateOf<SeccionEstudiante>(SeccionEstudiante.Inicio) }
    var menuAbierto by remember { mutableStateOf(false) }

    // Cada vez que se sale de un cuestionario, se relee el progreso desde la base
    LaunchedEffect(seccion) {
        if (seccion !is SeccionEstudiante.Cuestionario) viewModel.refrescar()
    }

    val nombre = estado.usuario?.nombre.orEmpty()
    val iniciales = nombre.split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }

    val esSubpantalla = seccion is SeccionEstudiante.Unidad ||
            seccion is SeccionEstudiante.Lectura ||
            seccion is SeccionEstudiante.Cuestionario

    val volver: () -> Unit = {
        seccion = when (val s = seccion) {
            is SeccionEstudiante.Cuestionario -> SeccionEstudiante.Unidad(s.oaCodigo)
            is SeccionEstudiante.Lectura -> SeccionEstudiante.Unidad(s.oaCodigo)
            is SeccionEstudiante.Unidad -> SeccionEstudiante.Asignaturas
            else -> SeccionEstudiante.Inicio
        }
    }
    BackHandler(enabled = esSubpantalla) { volver() }

    val abrirCuestionario: (String, ModoCuestionario) -> Unit = { oa, modo ->
        cuestionarioVm.iniciar(usuarioId, oa, modo)
        seccion = SeccionEstudiante.Cuestionario(oa, modo)
    }

    val indicePestana = when (seccion) {
        is SeccionEstudiante.Inicio -> 0
        is SeccionEstudiante.Progreso -> 2
        is SeccionEstudiante.Desafios -> 3
        else -> 1
    }

    Scaffold(
        containerColor = c.fondo,
        bottomBar = {
            BarraInferior(indicePestana) { indice ->
                seccion = when (indice) {
                    0 -> SeccionEstudiante.Inicio
                    1 -> SeccionEstudiante.Asignaturas
                    2 -> SeccionEstudiante.Progreso
                    else -> SeccionEstudiante.Desafios
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Encabezado
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(c.superficie)
                    .padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (esSubpantalla) {
                    IconButton(onClick = volver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = c.titulo)
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(c.marca, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) { Text("📖", fontSize = 18.sp) }
                    Spacer(Modifier.width(10.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("MPAZ PRO 6.º", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = c.titulo)
                    Text("Escuela Los Aromos · San Bernardo", color = c.textoSecundario, fontSize = 11.sp)
                }
                SelectorTema(tint = c.textoSecundario)
                Box {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(c.marcaSuave)
                            .clickable { menuAbierto = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(iniciales, color = c.titulo, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }
                    DropdownMenu(expanded = menuAbierto, onDismissRequest = { menuAbierto = false }) {
                        DropdownMenuItem(
                            text = { Text("Cerrar sesión") },
                            onClick = {
                                menuAbierto = false
                                onCerrarSesion()
                            }
                        )
                    }
                }
                Spacer(Modifier.width(8.dp))
            }

            // Contenido según la sección
            Box(modifier = Modifier.fillMaxSize()) {
                when (val s = seccion) {
                    is SeccionEstudiante.Inicio -> ContenidoInicio(
                        primerNombre = nombre.substringBefore(" "),
                        curso = estado.usuario?.curso.orEmpty(),
                        completadas = estado.completadas,
                        totales = estado.totales,
                        cantidadAsignaturas = estado.unidades.size,
                        detallePrueba = estado.pruebaDetalle,
                        onAsignaturas = { seccion = SeccionEstudiante.Asignaturas },
                        onProgreso = { seccion = SeccionEstudiante.Progreso },
                        onDesafios = { seccion = SeccionEstudiante.Desafios }
                    )

                    is SeccionEstudiante.Asignaturas -> PantallaAsignaturas(
                        unidades = estado.unidades,
                        onAbrir = { seccion = SeccionEstudiante.Unidad(it) }
                    )

                    is SeccionEstudiante.Unidad -> {
                        val unidad = estado.unidades.firstOrNull { it.oa.codigo == s.oaCodigo }
                        if (unidad != null) {
                            PantallaUnidad(
                                unidad = unidad,
                                onLectura = { seccion = SeccionEstudiante.Lectura(s.oaCodigo) },
                                onActividades = { abrirCuestionario(s.oaCodigo, ModoCuestionario.ACTIVIDADES) },
                                onDesafios = { abrirCuestionario(s.oaCodigo, ModoCuestionario.DESAFIOS) },
                                onPrueba = { abrirCuestionario(s.oaCodigo, ModoCuestionario.PRUEBA) }
                            )
                        }
                    }

                    is SeccionEstudiante.Lectura -> {
                        val unidad = estado.unidades.firstOrNull { it.oa.codigo == s.oaCodigo }
                        if (unidad != null) {
                            PantallaLectura(
                                unidad = unidad,
                                onContinuar = { abrirCuestionario(s.oaCodigo, ModoCuestionario.ACTIVIDADES) }
                            )
                        }
                    }

                    is SeccionEstudiante.Cuestionario -> when (cuestionario.fase) {
                        Fase.CARGANDO -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = c.marca)
                        }
                        Fase.PREGUNTANDO -> PantallaPregunta(
                            e = cuestionario,
                            onSeleccionar = cuestionarioVm::seleccionar,
                            onAccion = cuestionarioVm::accionPrincipal
                        )
                        Fase.RESULTADO -> PantallaResultado(
                            e = cuestionario,
                            onRevisar = cuestionarioVm::revisar,
                            onRepetir = cuestionarioVm::repetir,
                            onVolver = volver
                        )
                        Fase.REVISION -> PantallaRevision(
                            e = cuestionario,
                            onAnterior = cuestionarioVm::revisionAnterior,
                            onSiguiente = cuestionarioVm::revisionSiguiente,
                            onVolver = cuestionarioVm::volverAlResultado
                        )
                        Fase.BLOQUEADA -> PantallaBloqueada(
                            mensaje = cuestionario.mensaje.orEmpty(),
                            onVolver = volver
                        )
                    }

                    is SeccionEstudiante.Progreso -> PantallaProgreso(estado.unidades)

                    is SeccionEstudiante.Desafios -> PantallaDesafios(
                        unidades = estado.unidades,
                        onAbrir = { abrirCuestionario(it, ModoCuestionario.DESAFIOS) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ContenidoInicio(
    primerNombre: String,
    curso: String,
    completadas: Int,
    totales: Int,
    cantidadAsignaturas: Int,
    detallePrueba: String,
    onAsignaturas: () -> Unit,
    onProgreso: () -> Unit,
    onDesafios: () -> Unit
) {
    val c = Mpaz.colores
    val progreso = if (totales == 0) 0f else completadas.toFloat() / totales

    var objetivo by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(progreso) { objetivo = progreso }
    val progresoAnimado by animateFloatAsState(objetivo, tween(900), label = "progreso")

    var entrada by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { entrada = true }

    AnimatedVisibility(
        visible = entrada,
        enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { it / 12 }
    ) {
        Pagina {
            Etiqueta("6.º Básico · Curso $curso")
            Esp(12)
            Text("¡Hola, $primerNombre!", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = c.titulo)
            Esp(4)
            Text(
                "Cada paso cuenta. Hoy puedes seguir avanzando en tus asignaturas.",
                color = c.textoSecundario, fontSize = 14.sp
            )

            Esp(16)
            Tarjeta {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Tu progreso general", modifier = Modifier.weight(1f), fontSize = 14.sp, color = c.texto)
                    Text(
                        "${(progresoAnimado * 100).toInt()}%",
                        color = c.titulo, fontSize = 24.sp, fontWeight = FontWeight.Bold
                    )
                }
                Esp(8)
                BarraProgreso(progresoAnimado)
                Esp(8)
                Text(
                    "$completadas de $totales actividades y desafíos completados",
                    color = c.textoSecundario, fontSize = 12.sp
                )
            }

            Esp(20)
            Text("¿Qué quieres hacer?", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = c.titulo)
            Esp(12)
            TarjetaAccion(
                "Mis asignaturas", "Tus $cantidadAsignaturas espacios de aprendizaje",
                Modifier.fillMaxWidth(), onClick = onAsignaturas
            )
            Esp(12)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaAccion("Mi progreso", "Lo que ya has completado", Modifier.weight(1f), onClick = onProgreso)
                TarjetaAccion("Desafíos", "Practica con un nuevo reto", Modifier.weight(1f), onClick = onDesafios)
            }
            Esp(12)
            TarjetaAccion(
                "Prueba final", detallePrueba,
                Modifier.fillMaxWidth(), destacada = true, onClick = onAsignaturas
            )

            Esp(16)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(c.marcaSuave, RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Text(
                    "Aprender también es volver a intentar.",
                    color = c.titulo, fontWeight = FontWeight.SemiBold, fontSize = 13.sp
                )
                Esp(4)
                Text(
                    "Revisa tus respuestas y pregunta a tu docente cuando lo necesites.",
                    color = c.textoSecundario, fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun BarraInferior(indiceSeleccionado: Int, onSeleccionar: (Int) -> Unit) {
    val c = Mpaz.colores
    val items: List<Pair<ImageVector, String>> = listOf(
        Icons.Default.Home to "Inicio",
        Icons.AutoMirrored.Filled.MenuBook to "Asignaturas",
        Icons.Default.BarChart to "Progreso",
        Icons.Default.Flag to "Desafíos"
    )
    NavigationBar(containerColor = c.superficie) {
        items.forEachIndexed { indice, (icono, texto) ->
            NavigationBarItem(
                selected = indiceSeleccionado == indice,
                onClick = { onSeleccionar(indice) },
                icon = { Icon(icono, contentDescription = texto) },
                label = { Text(texto, fontSize = 11.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = c.sobreMarca,
                    selectedTextColor = c.titulo,
                    indicatorColor = c.marca,
                    unselectedIconColor = c.textoSecundario,
                    unselectedTextColor = c.textoSecundario
                )
            )
        }
    }
}
