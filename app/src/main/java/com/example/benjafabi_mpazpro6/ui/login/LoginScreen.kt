package com.example.benjafabi_mpazpro6.ui.login

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.benjafabi_mpazpro6.modelo.Rol
import com.example.benjafabi_mpazpro6.modelo.Usuario
import com.example.benjafabi_mpazpro6.ui.theme.Mpaz
import com.example.benjafabi_mpazpro6.ui.theme.SelectorTema



@Composable
fun LoginScreen(
    onLoginExitoso: (Usuario) -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsState()
    val c = Mpaz.colores
    var verClave by remember { mutableStateOf(false) }
    var entrada by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { entrada = true }
    LaunchedEffect(estado.usuarioLogueado) {
        estado.usuarioLogueado?.let(onLoginExitoso)
    }

    Surface(modifier = Modifier.fillMaxSize(), color = c.fondo, contentColor = c.texto) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                SelectorTema(tint = c.textoSecundario)
            }
            // Encabezado con animación de entrada
            AnimatedVisibility(
                visible = entrada,
                enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { it / 6 }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(c.marca, RoundedCornerShape(20.dp)),
                        contentAlignment = Alignment.Center
                    ) { Text("📖", fontSize = 32.sp) }

                    Spacer(Modifier.height(16.dp))
                    Text("MPAZ PRO 6.º", fontSize = 28.sp, fontWeight = FontWeight.Medium, color = c.titulo)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Un espacio para aprender, practicar y avanzar a tu ritmo.",
                        color = c.textoSecundario,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(28.dp))
            Text(
                "¡Te damos la bienvenida!",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = c.titulo,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                "Selecciona tu perfil para ingresar.",
                color = c.textoSecundario,
                fontSize = 13.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))
            SelectorDeRol(estado.rolSeleccionado, viewModel::onRolChange)

            Spacer(Modifier.height(20.dp))
            CampoTexto(
                etiqueta = "Usuario",
                valor = estado.nombreUsuario,
                onCambio = viewModel::onNombreUsuarioChange,
                placeholder = "Tu nombre de usuario",
                icono = Icons.Default.Person,
                error = estado.errorUsuario,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            Spacer(Modifier.height(12.dp))
            CampoTexto(
                etiqueta = "Contraseña",
                valor = estado.clave,
                onCambio = viewModel::onClaveChange,
                placeholder = "Tu contraseña",
                icono = Icons.Default.Lock,
                error = estado.errorClave,
                visualTransformation = if (verClave) VisualTransformation.None
                else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { viewModel.iniciarSesion() }),
                trailingIcon = {
                    IconButton(onClick = { verClave = !verClave }) {
                        Icon(
                            imageVector = if (verClave) Icons.Default.VisibilityOff
                            else Icons.Default.Visibility,
                            contentDescription = if (verClave) "Ocultar contraseña"
                            else "Mostrar contraseña"
                        )
                    }
                }
            )

            // Error general (perfil equivocado)
            Spacer(Modifier.height(8.dp))
            AnimatedContent(
                targetState = estado.errorGeneral,
                transitionSpec = {
                    (fadeIn(tween(200)) + expandVertically()) togetherWith
                            (fadeOut(tween(150)) + shrinkVertically())
                },
                label = "errorGeneral"
            ) { mensaje ->
                if (mensaje != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(c.incorrecto.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Warning, contentDescription = null,
                            tint = c.incorrecto, modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(mensaje, color = c.texto, fontSize = 13.sp)
                    }
                }
            }

            // Botón principal
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = viewModel::iniciarSesion,
                enabled = !estado.cargando,
                colors = ButtonDefaults.buttonColors(
                    containerColor = c.marca,
                    contentColor = c.sobreMarca,
                    disabledContainerColor = c.marca.copy(alpha = 0.6f),
                    disabledContentColor = c.sobreMarca
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Crossfade(targetState = estado.cargando, label = "botonLogin") { cargando ->
                    if (cargando) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = c.sobreMarca,
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(10.dp))
                            Text("Ingresando...", fontWeight = FontWeight.SemiBold)
                        }
                    } else {
                        Text("Iniciar sesión", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Tarjeta de acceso de ejemplo
            Spacer(Modifier.height(20.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(c.superficie, RoundedCornerShape(14.dp))
                    .border(1.dp, c.borde, RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(c.marca, RoundedCornerShape(50))
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        "EJEMPLO",
                        color = c.sobreMarca,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    "Acceso de ejemplo: Martina Torres",
                    color = c.titulo,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Usuario: martina6a · Clave: clave123",
                    color = c.textoSecundario,
                    fontSize = 12.sp
                )
                Text(
                    "Aplicación de apoyo escolar. Todos los datos son ficticios.",
                    color = c.textoSecundario,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun CampoTexto(
    etiqueta: String,
    valor: String,
    onCambio: (String) -> Unit,
    placeholder: String,
    icono: ImageVector,
    error: String?,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    trailingIcon: (@Composable () -> Unit)? = null
) {
    val c = Mpaz.colores
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            etiqueta,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = c.titulo,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        OutlinedTextField(
            value = valor,
            onValueChange = onCambio,
            placeholder = { Text(placeholder) },
            leadingIcon = { Icon(icono, contentDescription = null) },
            trailingIcon = trailingIcon,
            isError = error != null,
            supportingText = { MensajeError(error) },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = c.marca,
                unfocusedBorderColor = c.borde,
                errorBorderColor = c.incorrecto,
                focusedContainerColor = c.marcaSuave,
                unfocusedContainerColor = c.superficie,
                errorContainerColor = c.superficie,
                cursorColor = c.marca,
                errorCursorColor = c.incorrecto,
                focusedTextColor = c.texto,
                unfocusedTextColor = c.texto,
                errorTextColor = c.texto,
                focusedLeadingIconColor = c.marca,
                unfocusedLeadingIconColor = c.textoSecundario,
                errorLeadingIconColor = c.incorrecto
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun MensajeError(texto: String?) {
    AnimatedContent(
        targetState = texto,
        transitionSpec = {
            (fadeIn(tween(200)) + expandVertically()) togetherWith
                    (fadeOut(tween(150)) + shrinkVertically())
        },
        label = "mensajeError"
    ) { mensaje ->
        if (mensaje != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Warning, contentDescription = null,
                    tint = Mpaz.colores.incorrecto, modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(mensaje, color = Mpaz.colores.incorrecto, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun SelectorDeRol(seleccionado: Rol, onSeleccion: (Rol) -> Unit) {
    val c = Mpaz.colores
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(c.superficie, RoundedCornerShape(14.dp))
            .border(1.dp, c.borde, RoundedCornerShape(14.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Rol.entries.forEach { rol ->
            val activo = rol == seleccionado
            val fondo by animateColorAsState(
                if (activo) c.marcaSuave else Color.Transparent, tween(250), label = "fondoTab"
            )
            val colorTexto by animateColorAsState(
                if (activo) c.titulo else c.textoSecundario, tween(250), label = "textoTab"
            )
            val anchoBarra by animateDpAsState(
                if (activo) 22.dp else 0.dp, tween(250), label = "barraTab"
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(fondo, RoundedCornerShape(10.dp))
                    .clickable { onSeleccion(rol) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        rol.mostrarNombre,
                        color = colorTexto,
                        fontWeight = if (activo) FontWeight.SemiBold else FontWeight.Normal,
                        fontSize = 14.sp,
                        maxLines = 1
                    )
                    Spacer(Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .width(anchoBarra)
                            .height(3.dp)
                            .background(c.marca, RoundedCornerShape(50))
                    )
                }
            }
        }
    }
}