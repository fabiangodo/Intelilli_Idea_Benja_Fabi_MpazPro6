package com.example.benjafabi_mpazpro6.ui.estudiante

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.benjafabi_mpazpro6.recursos.Vibrador
import com.example.benjafabi_mpazpro6.ui.theme.Mpaz

// ───────────────────────── Piezas reutilizables ─────────────────────────

@Composable
internal fun Pagina(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        content = content
    )
}

@Composable
internal fun Esp(alto: Int = 12) = Spacer(Modifier.height(alto.dp))

@Composable
internal fun Etiqueta(texto: String) {
    val c = Mpaz.colores
    Box(
        modifier = Modifier
            .background(c.marcaSuave, RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) { Text(texto, color = c.titulo, fontSize = 12.sp) }
}

@Composable
internal fun Tarjeta(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    val c = Mpaz.colores
    val forma = RoundedCornerShape(16.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(c.superficie, forma)
            .border(1.dp, c.borde, forma)
            .padding(16.dp),
        content = content
    )
}

@Composable
internal fun TarjetaAccion(
    titulo: String,
    subtitulo: String,
    modifier: Modifier = Modifier,
    destacada: Boolean = false,
    onClick: () -> Unit
) {
    val c = Mpaz.colores
    val forma = RoundedCornerShape(16.dp)
    Column(
        modifier = modifier
            .clip(forma)
            .background(if (destacada) c.marcaSuave else c.superficie)
            .border(1.dp, if (destacada) c.marca else c.borde, forma)
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Text(titulo, color = c.titulo, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        Spacer(Modifier.height(2.dp))
        Text(subtitulo, color = c.textoSecundario, fontSize = 12.sp)
    }
}

@Composable
internal fun BarraProgreso(progreso: Float) {
    val c = Mpaz.colores
    LinearProgressIndicator(
        progress = { progreso },
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp),
        color = c.progreso,
        trackColor = c.borde,
        strokeCap = StrokeCap.Round
    )
}

@Composable
private fun acentoDe(asignatura: String): Color {
    val c = Mpaz.colores
    return when {
        asignatura.startsWith("Lenguaje") -> c.marca
        asignatura.startsWith("Matem") -> c.info
        asignatura.startsWith("Ciencias") -> c.progreso
        else -> c.logro
    }
}

private fun iconoDe(asignatura: String): ImageVector = when {
    asignatura.startsWith("Matem") -> Icons.Default.Calculate
    asignatura.startsWith("Lenguaje") -> Icons.AutoMirrored.Filled.MenuBook
    asignatura.startsWith("Ciencias") -> Icons.Default.Eco
    else -> Icons.Default.Public
}

private fun textoIntentos(n: Int) = if (n == 1) "1 intento" else "$n intentos"

/** Muestra una imagen de res/drawable por su nombre. Si el archivo no existe, no muestra nada (no se cae). */
@Composable
private fun ImagenContenido(nombre: String?) {
    if (nombre.isNullOrBlank()) return
    val contexto = LocalContext.current
    val id = remember(nombre) {
        contexto.resources.getIdentifier(nombre, "drawable", contexto.packageName)
    }
    if (id != 0) {
        Image(
            painter = painterResource(id),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
        )
        Esp(12)
    }
}

// ───────────────────────── Asignaturas y unidad ─────────────────────────

@Composable
fun PantallaAsignaturas(unidades: List<UnidadUi>, onAbrir: (String) -> Unit) {
    val c = Mpaz.colores
    Pagina {
        Text("Cuatro formas de aprender", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = c.titulo)
        Esp(4)
        Text(
            "Explora tu unidad asignada. Cada asignatura tiene su propio espacio.",
            color = c.textoSecundario, fontSize = 13.sp
        )
        Esp(20)
        if (unidades.isEmpty()) {
            Text("Tu docente todavía no te asigna contenidos.", color = c.textoSecundario)
        }
        unidades.forEach { u ->
            val acento = acentoDe(u.oa.asignatura)
            Tarjeta(modifier = Modifier.clickable { onAbrir(u.oa.codigo) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(acento.copy(alpha = 0.18f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) { Icon(iconoDe(u.oa.asignatura), contentDescription = null, tint = acento) }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 14.dp)
                    ) {
                        Text(u.oa.asignatura, fontWeight = FontWeight.Bold, color = c.titulo, fontSize = 15.sp)
                        Text(u.oa.unidad, color = c.textoSecundario, fontSize = 12.sp)
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = c.textoSecundario)
                }
            }
            Esp(12)
        }
    }
}

@Composable
fun PantallaUnidad(
    unidad: UnidadUi,
    onLectura: () -> Unit,
    onActividades: () -> Unit,
    onDesafios: () -> Unit,
    onPrueba: () -> Unit
) {
    val c = Mpaz.colores
    val oa = unidad.oa
    val detallePrueba = when {
        !unidad.habilitada -> "Aún no habilitada por tu docente"
        unidad.intentosRestantes == 0 -> "Usaste tus ${textoIntentos(unidad.maxIntentos)}"
        else -> "Hasta ${textoIntentos(unidad.maxIntentos)} · te quedan ${unidad.intentosRestantes}"
    }

    Pagina {
        Etiqueta("${oa.eje} · Publicado")
        Esp(8)
        Text(oa.asignatura, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = c.titulo)
        Text(oa.unidad, fontSize = 16.sp, color = c.textoSecundario)

        Esp(16)
        Tarjeta {
            Text("Objetivo de Aprendizaje", fontWeight = FontWeight.Bold, color = c.titulo, fontSize = 14.sp)
            Esp(6)
            Text("${oa.codigo}: ${oa.descripcion}", fontSize = 12.sp, color = c.texto)
        }

        Esp(20)
        Text(
            "Mi progreso en la unidad · ${(unidad.progreso * 100).toInt()}%",
            fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = c.titulo
        )
        Esp(6)
        BarraProgreso(unidad.progreso)

        Esp(20)
        TarjetaAccion(
            "Contenido y explicaciones", "Lectura y ejemplo para ${oa.codigo}",
            Modifier.fillMaxWidth(), onClick = onLectura
        )
        Esp(12)
        TarjetaAccion(
            "Actividades prácticas",
            "Práctica paso a paso · ${unidad.actividadesHechas} de ${unidad.actividadesTotal} listas",
            Modifier.fillMaxWidth(), onClick = onActividades
        )
        Esp(12)
        TarjetaAccion(
            "Desafíos",
            "Retos más difíciles · ${unidad.desafiosHechos} de ${unidad.desafiosTotal} resueltos",
            Modifier.fillMaxWidth(), onClick = onDesafios
        )
        Esp(12)
        TarjetaAccion(
            "Prueba final evaluada", detallePrueba,
            Modifier.fillMaxWidth(), destacada = true, onClick = onPrueba
        )
    }
}

@Composable
fun PantallaLectura(unidad: UnidadUi, onContinuar: () -> Unit) {
    val c = Mpaz.colores
    val contenido = unidad.contenido

    Pagina {
        Etiqueta("${unidad.oa.codigo} · ${unidad.oa.asignatura}")
        Esp(12)
        if (contenido == null) {
            Text("Esta unidad todavía no tiene contenido.", color = c.textoSecundario)
        } else {
            Text(contenido.titulo, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = c.titulo)
            Esp(10)
            ImagenContenido(contenido.imagen)
            Text(contenido.explicacion, fontSize = 15.sp, lineHeight = 23.sp, color = c.texto)
            Esp(16)
            Tarjeta {
                Text("Ejemplo", fontWeight = FontWeight.Bold, color = c.titulo, fontSize = 14.sp)
                Esp(6)
                Text(contenido.ejemplo, fontSize = 14.sp, lineHeight = 21.sp, color = c.texto)
            }
            Esp(12)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(c.marcaSuave, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Text("Cómo trabajar", fontWeight = FontWeight.Bold, color = c.titulo, fontSize = 14.sp)
                Esp(6)
                Text(contenido.instrucciones, fontSize = 13.sp, color = c.texto)
            }
        }
        Esp(24)
        BotonPrincipal("Ir a las actividades", onContinuar)
    }
}

// ───────────────────────── Cuestionario ─────────────────────────

@Composable
private fun BotonPrincipal(texto: String, onClick: () -> Unit, enabled: Boolean = true) {
    val c = Mpaz.colores
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = c.marca,
            contentColor = c.sobreMarca,
            disabledContainerColor = c.borde,
            disabledContentColor = c.textoSecundario
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    ) { Text(texto, fontWeight = FontWeight.Bold) }
}

@Composable
private fun BotonSecundario(texto: String, onClick: () -> Unit, enabled: Boolean = true, modifier: Modifier = Modifier) {
    val c = Mpaz.colores
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.outlinedButtonColors(contentColor = c.titulo),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.height(48.dp)
    ) { Text(texto) }
}

@Composable
fun PantallaPregunta(e: CuestionarioUiState, onSeleccionar: (Int) -> Unit, onAccion: () -> Unit) {
    val c = Mpaz.colores
    val contexto = LocalContext.current
    val item = e.items[e.indice]
    val acierto = e.comprobada && e.seleccion == item.posicionCorrecta
    val esUltima = e.indice == e.items.lastIndex
    val avance = (e.indice + if (e.comprobada) 1 else 0).toFloat() / e.total

    // Recurso del dispositivo: vibración al comprobar una respuesta de práctica
    LaunchedEffect(e.comprobada, e.indice) {
        if (e.comprobada) {
            if (acierto) Vibrador.correcto(contexto) else Vibrador.incorrecto(contexto)
        }
    }

    val textoBoton = when {
        e.modo.feedbackInmediato && !e.comprobada -> "Responder"
        esUltima -> if (e.modo.feedbackInmediato) "Ver resultado" else "Entregar prueba"
        else -> "Siguiente"
    }

    Pagina {
        Etiqueta("${e.oa?.codigo.orEmpty()} · ${e.modo.titulo}")
        Esp(10)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Pregunta ${e.indice + 1} de ${e.total}",
                fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = c.textoSecundario,
                modifier = Modifier.weight(1f)
            )
            if (e.modo == ModoCuestionario.PRUEBA) {
                Text(
                    "Intento ${e.intentoNumero} de ${e.maxIntentos}",
                    fontSize = 12.sp, color = c.textoSecundario
                )
            }
        }
        Esp(6)
        BarraProgreso(avance)

        Esp(16)
        Text(item.pregunta.enunciado, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = c.titulo, lineHeight = 27.sp)

        Esp(20)
        item.textos.forEachIndexed { pos, texto ->
            OpcionRespuesta(
                letra = 'A' + pos,
                texto = texto,
                elegida = e.seleccion == pos,
                esCorrecta = e.comprobada && pos == item.posicionCorrecta,
                esError = e.comprobada && e.seleccion == pos && pos != item.posicionCorrecta,
                onClick = { onSeleccionar(pos) }
            )
            Esp(10)
        }

        AnimatedVisibility(visible = e.comprobada) {
            val color = if (acierto) c.correcto else c.incorrecto
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color.copy(alpha = 0.12f), RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (acierto) Icons.Default.CheckCircle else Icons.Default.Cancel,
                        contentDescription = null, tint = color
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (acierto) "¡Correcto!" else "Casi. La respuesta correcta es: ${item.textoCorrecto}",
                        fontWeight = FontWeight.Bold, color = c.texto, fontSize = 14.sp
                    )
                }
                if (item.pregunta.retroalimentacion.isNotBlank()) {
                    Esp(6)
                    Text(item.pregunta.retroalimentacion, fontSize = 13.sp, color = c.texto)
                }
            }
        }

        if (!e.modo.feedbackInmediato) {
            Text(
                "En la prueba final verás qué respondiste bien y mal cuando la entregues.",
                fontSize = 12.sp, color = c.textoSecundario
            )
        }

        Esp(16)
        BotonPrincipal(textoBoton, onAccion, enabled = e.seleccion != null)
    }
}

@Composable
private fun OpcionRespuesta(
    letra: Char,
    texto: String,
    elegida: Boolean,
    esCorrecta: Boolean,
    esError: Boolean,
    onClick: () -> Unit
) {
    val c = Mpaz.colores
    val colorBorde by animateColorAsState(
        when {
            esCorrecta -> c.correcto
            esError -> c.incorrecto
            elegida -> c.marca
            else -> c.borde
        },
        label = "bordeOpcion"
    )
    val fondo = when {
        esCorrecta -> c.correcto.copy(alpha = 0.12f)
        esError -> c.incorrecto.copy(alpha = 0.12f)
        elegida -> c.marcaSuave
        else -> c.superficie
    }
    val forma = RoundedCornerShape(12.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(forma)
            .background(fondo)
            .border(if (elegida || esCorrecta || esError) 2.dp else 1.dp, colorBorde, forma)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("$letra", fontWeight = FontWeight.Bold, color = c.titulo)
        Spacer(Modifier.width(16.dp))
        Text(texto, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = c.texto, modifier = Modifier.weight(1f))
        when {
            esCorrecta -> Icon(Icons.Default.CheckCircle, contentDescription = "Correcta", tint = c.correcto)
            esError -> Icon(Icons.Default.Cancel, contentDescription = "Incorrecta", tint = c.incorrecto)
        }
    }
}

@Composable
fun PantallaResultado(
    e: CuestionarioUiState,
    onRevisar: () -> Unit,
    onRepetir: () -> Unit,
    onVolver: () -> Unit
) {
    val c = Mpaz.colores
    val esPrueba = e.modo == ModoCuestionario.PRUEBA
    val correctas = e.correctas
    val incorrectas = e.total - correctas
    val titulo = when (e.modo) {
        ModoCuestionario.ACTIVIDADES -> "¡Actividades completadas!"
        ModoCuestionario.DESAFIOS -> "¡Desafíos completados!"
        ModoCuestionario.PRUEBA -> "¡Prueba completada!"
    }

    Pagina {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(c.correcto.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = c.correcto, modifier = Modifier.size(40.dp))
            }
            Esp(12)
            Text(titulo, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = c.titulo, textAlign = TextAlign.Center)
            Esp(6)
            Etiqueta(
                if (esPrueba) "Intento ${e.intentoNumero} de ${e.maxIntentos} · Finalizado"
                else "${e.oa?.codigo.orEmpty()} · Progreso guardado"
            )
        }

        Esp(20)
        Tarjeta {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$correctas de ${e.total}", fontSize = 40.sp, fontWeight = FontWeight.Bold, color = c.titulo)
                Text("Respuestas correctas", color = c.textoSecundario, fontSize = 12.sp)
            }
            Esp(16)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ConteoCaja("$correctas correctas", true, Modifier.weight(1f))
                ConteoCaja("$incorrectas incorrectas", false, Modifier.weight(1f))
            }
        }

        Esp(16)
        Text(
            "Revisa las explicaciones y sigue practicando. Equivocarse es parte de aprender.",
            color = c.textoSecundario, fontSize = 13.sp
        )

        if (esPrueba && e.intentosRestantes > 0) {
            Esp(12)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(c.marcaSuave, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Text(
                    "Te quedan ${textoIntentos(e.intentosRestantes)}",
                    fontWeight = FontWeight.Bold, color = c.titulo, fontSize = 13.sp
                )
                Text(
                    "Revisar las respuestas no usa un nuevo intento.",
                    color = c.textoSecundario, fontSize = 12.sp
                )
            }
        }

        Esp(20)
        BotonPrincipal("Revisar respuestas", onRevisar)
        Esp(10)
        when {
            !esPrueba -> BotonSecundario("Practicar de nuevo", onRepetir, modifier = Modifier.fillMaxWidth())
            e.intentosRestantes > 0 -> BotonSecundario(
                "Repetir prueba · ${textoIntentos(e.intentosRestantes)} restante${if (e.intentosRestantes == 1) "" else "s"}",
                onRepetir, modifier = Modifier.fillMaxWidth()
            )
            else -> Text(
                "Ya usaste todos tus intentos de esta prueba.",
                color = c.textoSecundario, fontSize = 13.sp, modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
        Esp(10)
        BotonSecundario("Volver a la unidad", onVolver, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun ConteoCaja(texto: String, correcta: Boolean, modifier: Modifier) {
    val c = Mpaz.colores
    val color = if (correcta) c.correcto else c.incorrecto
    Row(
        modifier = modifier
            .background(color.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            if (correcta) Icons.Default.CheckCircle else Icons.Default.Cancel,
            contentDescription = null, tint = color, modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(6.dp))
        Text(texto, color = c.texto, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}

@Composable
fun PantallaRevision(
    e: CuestionarioUiState,
    onAnterior: () -> Unit,
    onSiguiente: () -> Unit,
    onVolver: () -> Unit
) {
    val c = Mpaz.colores
    val item = e.items[e.indiceRevision]
    val alternativas = item.pregunta.listaAlternativas()
    val elegida = e.elegidas[item.pregunta.id] ?: -1
    val acerto = elegida == item.pregunta.indiceCorrecta

    Pagina {
        Etiqueta("${e.correctas} correctas · ${e.total - e.correctas} incorrectas")
        Esp(12)
        Tarjeta {
            Text(
                "Ítem ${e.indiceRevision + 1} de ${e.total} · Respuesta ${if (acerto) "correcta" else "incorrecta"}",
                fontWeight = FontWeight.SemiBold, color = c.titulo, fontSize = 13.sp
            )
            Text(e.oa?.codigo.orEmpty(), color = c.textoSecundario, fontSize = 12.sp)
        }

        Esp(16)
        Text(item.pregunta.enunciado, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = c.titulo, lineHeight = 27.sp)

        Esp(16)
        Text("Tu respuesta", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = c.textoSecundario)
        Esp(6)
        CajaRespuesta(alternativas.getOrNull(elegida) ?: "Sin respuesta", acerto)

        if (!acerto) {
            Esp(12)
            Text("Respuesta correcta", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = c.textoSecundario)
            Esp(6)
            CajaRespuesta(item.textoCorrecto, true)
        }

        Esp(16)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(c.marcaSuave, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Text("¿Por qué?", fontWeight = FontWeight.Bold, color = c.titulo, fontSize = 14.sp)
            Esp(6)
            Text(
                item.pregunta.retroalimentacion.ifBlank { "La respuesta correcta es: ${item.textoCorrecto}." },
                fontSize = 13.sp, color = c.texto
            )
        }

        Esp(16)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            BotonSecundario("Anterior", onAnterior, enabled = e.indiceRevision > 0, modifier = Modifier.weight(1f))
            BotonSecundario("Siguiente", onSiguiente, enabled = e.indiceRevision < e.items.lastIndex, modifier = Modifier.weight(1f))
        }
        Esp(10)
        BotonPrincipal("Volver al resultado", onVolver)
    }
}

@Composable
private fun CajaRespuesta(texto: String, correcta: Boolean) {
    val c = Mpaz.colores
    val color = if (correcta) c.correcto else c.incorrecto
    val forma = RoundedCornerShape(12.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(forma)
            .background(color.copy(alpha = 0.12f))
            .border(2.dp, color, forma)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(texto, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = c.texto, modifier = Modifier.weight(1f))
        Spacer(Modifier.width(8.dp))
        Icon(
            if (correcta) Icons.Default.CheckCircle else Icons.Default.Cancel,
            contentDescription = null, tint = color
        )
        Spacer(Modifier.width(4.dp))
        Text(if (correcta) "Correcta" else "Incorrecta", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = c.texto)
    }
}

@Composable
fun PantallaBloqueada(mensaje: String, onVolver: () -> Unit) {
    val c = Mpaz.colores
    Pagina {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Esp(40)
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(c.marcaSuave, CircleShape),
                contentAlignment = Alignment.Center
            ) { Icon(Icons.Default.Lock, contentDescription = null, tint = c.titulo, modifier = Modifier.size(32.dp)) }
            Esp(16)
            Text("Por ahora no está disponible", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = c.titulo)
            Esp(8)
            Text(mensaje, color = c.textoSecundario, fontSize = 14.sp, textAlign = TextAlign.Center)
            Esp(24)
        }
        BotonPrincipal("Volver a la unidad", onVolver)
    }
}

// ───────────────────────── Progreso y desafíos ─────────────────────────

@Composable
fun PantallaProgreso(unidades: List<UnidadUi>) {
    val c = Mpaz.colores
    Pagina {
        Text("Mi progreso", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = c.titulo)
        Esp(4)
        Text("Lo que ya has completado en cada asignatura.", color = c.textoSecundario, fontSize = 13.sp)
        Esp(16)
        unidades.forEach { u ->
            val prueba = if (u.intentosUsados == 0) {
                "Prueba final: aún sin rendir"
            } else {
                "Prueba final: ${u.ultimoCorrectas} de ${u.ultimoTotal} · intento ${u.intentosUsados} de ${u.maxIntentos}"
            }
            Tarjeta {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(iconoDe(u.oa.asignatura), contentDescription = null, tint = acentoDe(u.oa.asignatura))
                    Spacer(Modifier.width(10.dp))
                    Text(u.oa.asignatura, fontWeight = FontWeight.Bold, color = c.titulo, modifier = Modifier.weight(1f))
                    Text("${(u.progreso * 100).toInt()}%", fontWeight = FontWeight.Bold, color = c.titulo)
                }
                Esp(8)
                BarraProgreso(u.progreso)
                Esp(8)
                Text(
                    "Actividades: ${u.actividadesHechas} de ${u.actividadesTotal} · Desafíos: ${u.desafiosHechos} de ${u.desafiosTotal}",
                    fontSize = 12.sp, color = c.textoSecundario
                )
                Text(prueba, fontSize = 12.sp, color = c.textoSecundario)
            }
            Esp(12)
        }
    }
}

@Composable
fun PantallaDesafios(unidades: List<UnidadUi>, onAbrir: (String) -> Unit) {
    val c = Mpaz.colores
    Pagina {
        Text("Desafíos", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = c.titulo)
        Esp(4)
        Text("Retos para poner a prueba lo que aprendiste.", color = c.textoSecundario, fontSize = 13.sp)
        Esp(16)
        unidades.forEach { u ->
            TarjetaAccion(
                titulo = "Desafío de ${u.oa.asignatura}",
                subtitulo = "${u.desafiosHechos} de ${u.desafiosTotal} resueltos",
                modifier = Modifier.fillMaxWidth(),
                onClick = { onAbrir(u.oa.codigo) }
            )
            Esp(12)
        }
    }
}
