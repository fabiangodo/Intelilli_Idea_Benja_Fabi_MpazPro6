package com.example.benjafabi_mpazpro6.ui.utp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.benjafabi_mpazpro6.modelo.AppDatabase
import com.example.benjafabi_mpazpro6.modelo.EstadoContenido
import com.example.benjafabi_mpazpro6.ui.theme.Mpaz
import com.example.benjafabi_mpazpro6.ui.theme.SelectorTema
import kotlinx.coroutines.launch

data class ContenidoUTP(
    val id: String,
    val oa: String,
    val titulo: String,
    val estado: EstadoContenido
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PanelUtpScreen(
    usuarioId: String,
    onCerrarSesion: () -> Unit
) {
    val c = Mpaz.colores
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val db = remember { AppDatabase.getDatabase(context) }
    val dao = db.curricularDao()
    var menuAbierto by remember { mutableStateOf(false) }

    var listaContenidos by remember {
        mutableStateOf(
            listOf(
                ContenidoUTP("1", "OA 03", "Concepto de Razones", EstadoContenido.EN_REVISION),
                ContenidoUTP("2", "OA 03", "Ejercicios Prácticos de Razones", EstadoContenido.BORRADOR),
                ContenidoUTP("3", "OA 05", "Comprensión de Textos Narrativos", EstadoContenido.AUTORIZADO),
                ContenidoUTP("4", "OA 01", "Sistemas del Cuerpo Humano", EstadoContenido.PUBLICADO)
            )
        )
    }

    LaunchedEffect(Unit) {
        val configDb = dao.configDe("OA 03")
        if (configDb != null) {
            listaContenidos = listaContenidos.map { item ->
                if (item.oa == "OA 03" && item.id == "1") {
                    item.copy(
                        titulo = "Prueba Final (${configDb.maxIntentos} intentos propuestos)",
                        estado = configDb.estado
                    )
                } else {
                    item
                }
            }
        }
    }

    Scaffold(
        containerColor = c.fondo
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Encabezado adaptado con selector de tema
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(c.superficie)
                    .padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(c.marca, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) { Text("🏛️", fontSize = 18.sp) }

                Spacer(Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text("Panel UTP", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = c.titulo)
                    Text("Gestión Curricular y Validación", color = c.textoSecundario, fontSize = 11.sp)
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
                        Text("UTP", color = c.titulo, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Estado de Contenidos por OA",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = c.titulo,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                val agrupados = listaContenidos.groupBy { it.oa }

                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    agrupados.forEach { (oa, contenidos) ->
                        item {
                            Text(
                                text = oa,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = c.marca,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                        items(contenidos) { item ->
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn() + slideInVertically()
                            ) {
                                ItemContenidoUTP(
                                    item = item,
                                    onCambiarEstado = { nuevoEstado ->
                                        // Vibración nativa
                                        val vibrator = context.getSystemService(android.content.Context.VIBRATOR_SERVICE) as? android.os.Vibrator
                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                            vibrator?.vibrate(android.os.VibrationEffect.createOneShot(100, android.os.VibrationEffect.DEFAULT_AMPLITUDE))
                                        } else {
                                            @Suppress("DEPRECATION")
                                            vibrator?.vibrate(100)
                                        }

                                        scope.launch {
                                            dao.actualizarEstadoConfigPrueba(item.oa, nuevoEstado)
                                            dao.actualizarEstadoOa(item.oa, nuevoEstado)

                                            listaContenidos = listaContenidos.map {
                                                if (it.id == item.id) it.copy(estado = nuevoEstado) else it
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ItemContenidoUTP(
    item: ContenidoUTP,
    onCambiarEstado: (EstadoContenido) -> Unit
) {
    val c = Mpaz.colores

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = c.superficie),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = item.titulo, fontWeight = FontWeight.Bold, color = c.titulo, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Estado: ", fontSize = 13.sp, color = c.textoSecundario)
                Text(
                    text = item.estado.mostrarNombre,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (item.estado) {
                        EstadoContenido.PUBLICADO -> c.correcto
                        EstadoContenido.AUTORIZADO -> c.marca
                        EstadoContenido.EN_REVISION -> c.advertencia
                        EstadoContenido.BORRADOR -> c.textoSecundario
                    }
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { onCambiarEstado(EstadoContenido.AUTORIZADO) },
                    colors = ButtonDefaults.buttonColors(containerColor = c.marca),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Autorizar", fontSize = 12.sp, color = c.sobreMarca)
                }
                Button(
                    onClick = { onCambiarEstado(EstadoContenido.PUBLICADO) },
                    enabled = item.estado == EstadoContenido.AUTORIZADO,
                    colors = ButtonDefaults.buttonColors(containerColor = c.correcto),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Publicar", fontSize = 12.sp, color = Color.White)
                }
            }
        }
    }
}