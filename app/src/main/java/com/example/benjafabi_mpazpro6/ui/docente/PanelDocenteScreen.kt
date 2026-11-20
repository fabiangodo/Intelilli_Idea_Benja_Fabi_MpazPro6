package com.example.benjafabi_mpazpro6.ui.docente

import android.widget.Toast
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.benjafabi_mpazpro6.modelo.AppDatabase
import com.example.benjafabi_mpazpro6.modelo.ConfigPrueba
import com.example.benjafabi_mpazpro6.modelo.EstadoContenido
import com.example.benjafabi_mpazpro6.ui.theme.Mpaz
import com.example.benjafabi_mpazpro6.ui.theme.SelectorTema
import kotlinx.coroutines.launch

data class InformeEstudiante(
    val id: String,
    val nombreEstudiante: String,
    val oa: String,
    val porcentaje: Int,
    val observacion: String,
    val estado: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PanelDocenteScreen(
    usuarioId: String,
    onCerrarSesion: () -> Unit
) {
    val c = Mpaz.colores
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val db = remember { AppDatabase.getDatabase(context) }
    val dao = db.curricularDao()

    val oaSeleccionado = "OA 03"

    var intentosInput by remember { mutableStateOf("") }
    var errorIntentos by remember { mutableStateOf<String?>(null) }
    var guardadoExitoso by remember { mutableStateOf(false) }
    var menuAbierto by remember { mutableStateOf(false) }

    LaunchedEffect(oaSeleccionado) {
        val configExistente = dao.configDe(oaSeleccionado)
        if (configExistente != null) {
            intentosInput = configExistente.maxIntentos.toString()
            guardadoExitoso = configExistente.habilitada
        }
    }

    var listaInformes by remember {
        mutableStateOf(
            listOf(
                InformeEstudiante("1", "Juan Pérez", "OA 03 Matemática", 85, "Fortaleza: Comprensión de razones", "generado"),
                InformeEstudiante("2", "María Gómez", "OA 03 Matemática", 45, "Necesita refuerzo: Simplificación de razones", "pendiente de validacion")
            )
        )
    }

    Scaffold(
        containerColor = c.fondo
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Encabezado alineado al diseño del Estudiante
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
                ) { Text("👨‍🏫", fontSize = 18.sp) }

                Spacer(Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text("Panel Docente", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = c.titulo)
                    Text("Gestión de Evaluaciones y Contenidos", color = c.textoSecundario, fontSize = 11.sp)
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
                        Text("DOC", color = c.titulo, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
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

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = c.superficie),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("1. Asignar Contenidos a OA", fontWeight = FontWeight.Bold, color = c.titulo, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    Toast.makeText(context, "Para una futura actualización", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = c.marca)
                            ) {
                                Text("Seleccionar OA", color = c.sobreMarca)
                            }
                        }
                    }
                }

                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = c.superficie),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("2. Configurar Intentos de Prueba ($oaSeleccionado)", fontWeight = FontWeight.Bold, color = c.titulo, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = intentosInput,
                                onValueChange = {
                                    intentosInput = it
                                    guardadoExitoso = false
                                    val num = it.toIntOrNull()
                                    errorIntentos = when {
                                        it.isBlank() -> "Campo obligatorio"
                                        num == null || num !in 1..5 -> "Debe ser entre 1 y 5"
                                        else -> null
                                    }
                                },
                                label = { Text("Máximo de intentos (1-5)", color = c.textoSecundario) },
                                isError = errorIntentos != null,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                trailingIcon = {
                                    if (errorIntentos != null) {
                                        Icon(Icons.Filled.Warning, contentDescription = "Error", tint = c.incorrecto)
                                    } else if (guardadoExitoso) {
                                        Icon(Icons.Filled.Check, contentDescription = "Guardado", tint = c.correcto)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            )

                            if (errorIntentos != null) {
                                Text(
                                    text = errorIntentos!!,
                                    color = c.incorrecto,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    val num = intentosInput.toIntOrNull()
                                    if (num != null && num in 1..5) {
                                        errorIntentos = null
                                        scope.launch {
                                            dao.insertarConfig(
                                                ConfigPrueba(
                                                    oaCodigo = oaSeleccionado,
                                                    maxIntentos = num,
                                                    habilitada = true,
                                                    estado = EstadoContenido.EN_REVISION
                                                )
                                            )
                                            guardadoExitoso = true
                                        }
                                    } else if (intentosInput.isBlank()) {
                                        errorIntentos = "Campo obligatorio"
                                    } else {
                                        errorIntentos = "Debe ser entre 1 y 5"
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = c.marca),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Guardar y Enviar a UTP", color = c.sobreMarca, fontWeight = FontWeight.Bold)
                            }

                            AnimatedVisibility(visible = guardadoExitoso) {
                                Text(
                                    "✓ Configuración guardada localmente y enviada a revisión UTP.",
                                    color = c.correcto,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    }
                }

                item {
                    Text("3. Informes de Estudiantes", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = c.titulo)
                }

                items(listaInformes) { informe ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = c.superficie),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(informe.nombreEstudiante, fontWeight = FontWeight.Bold, color = c.titulo, fontSize = 15.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("${informe.oa} | Logro: ${informe.porcentaje}%", fontSize = 13.sp, color = c.textoSecundario)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(informe.observacion, fontSize = 13.sp, color = c.texto)

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp)
                            ) {
                                Text("Estado: ${informe.estado}", fontSize = 12.sp, color = c.textoSecundario, fontWeight = FontWeight.SemiBold)

                                if (informe.estado != "validado") {
                                    Button(
                                        onClick = {
                                            listaInformes = listaInformes.map {
                                                if (it.id == informe.id) it.copy(estado = "validado") else it
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = c.correcto),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Validar", color = Color.White, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}