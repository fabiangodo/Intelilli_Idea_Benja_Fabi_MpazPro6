package com.example.benjafabi_mpazpro6.ui.estudiante

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.example.benjafabi_mpazpro6.modelo.AppDatabase
import com.example.benjafabi_mpazpro6.modelo.EstadoInforme
import com.example.benjafabi_mpazpro6.modelo.Informe
import com.example.benjafabi_mpazpro6.modelo.Intento
import com.example.benjafabi_mpazpro6.modelo.ObjetivoAprendizaje
import com.example.benjafabi_mpazpro6.modelo.Pregunta
import com.example.benjafabi_mpazpro6.modelo.Respuesta
import com.example.benjafabi_mpazpro6.modelo.TipoPregunta
import com.example.benjafabi_mpazpro6.validacion.ReglaInforme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Los tres tipos de cuestionario de una unidad. Solo la prueba final limita intentos y guarda informe. */
enum class ModoCuestionario(
    val titulo: String,
    val tipo: TipoPregunta,
    val feedbackInmediato: Boolean
) {
    ACTIVIDADES("Actividades", TipoPregunta.ACTIVIDAD, true),
    DESAFIOS("Desafíos", TipoPregunta.DESAFIO, true),
    PRUEBA("Prueba final", TipoPregunta.PRUEBA_FINAL, false)
}

enum class Fase { CARGANDO, PREGUNTANDO, RESULTADO, REVISION, BLOQUEADA }

data class ItemPregunta(val pregunta: Pregunta, val orden: List<Int>) {
    val textos: List<String> = pregunta.listaAlternativas().let { alts -> orden.map { alts[it] } }
    val posicionCorrecta: Int = orden.indexOf(pregunta.indiceCorrecta)
    val textoCorrecto: String get() = pregunta.listaAlternativas()[pregunta.indiceCorrecta]
}

data class CuestionarioUiState(
    val fase: Fase = Fase.CARGANDO,
    val modo: ModoCuestionario = ModoCuestionario.ACTIVIDADES,
    val oa: ObjetivoAprendizaje? = null,
    val items: List<ItemPregunta> = emptyList(),
    val indice: Int = 0,
    val seleccion: Int? = null,                   // posición mostrada que eligió el estudiante
    val comprobada: Boolean = false,              // en práctica: ya se mostró la retroalimentación
    val elegidas: Map<Int, Int> = emptyMap(),     // idPregunta -> índice ORIGINAL elegido
    val intentoNumero: Int = 0,
    val maxIntentos: Int = 0,
    val intentosRestantes: Int = 0,
    val mensaje: String? = null,                  // motivo si la sección está bloqueada
    val indiceRevision: Int = 0
) {
    val total: Int get() = items.size
    val correctas: Int get() = items.count { elegidas[it.pregunta.id] == it.pregunta.indiceCorrecta }
}

class CuestionarioViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)

    private val _estado = MutableStateFlow(CuestionarioUiState())
    val estado: StateFlow<CuestionarioUiState> = _estado.asStateFlow()

    private var usuarioId = ""
    private var oaCodigo = ""
    private var modo = ModoCuestionario.ACTIVIDADES
    private var guardando = false

    fun iniciar(usuarioId: String, oaCodigo: String, modo: ModoCuestionario) {
        this.usuarioId = usuarioId
        this.oaCodigo = oaCodigo
        this.modo = modo
        _estado.value = CuestionarioUiState(modo = modo)

        viewModelScope.launch {
            val curricular = db.curricularDao()
            val oa = curricular.obtenerOa(oaCodigo)
            val preguntas = curricular.preguntasDe(oaCodigo, modo.tipo)

            var max = 0
            var usados = 0
            var bloqueo: String? = null

            if (modo == ModoCuestionario.PRUEBA) {
                val config = curricular.configDe(oaCodigo)
                usados = db.progresoDao().intentosUsados(usuarioId, oaCodigo)
                max = config?.maxIntentos ?: 0
                bloqueo = when {
                    config == null || !config.habilitada -> "Tu docente aún no habilita esta prueba."
                    usados >= max -> "Ya usaste los $max intentos de esta prueba."
                    else -> null
                }
            }
            if (bloqueo == null && preguntas.isEmpty()) {
                bloqueo = "Todavía no hay preguntas en esta sección."
            }

            _estado.value = CuestionarioUiState(
                fase = if (bloqueo != null) Fase.BLOQUEADA else Fase.PREGUNTANDO,
                modo = modo,
                oa = oa,
                items = preguntas.map { p ->
                    ItemPregunta(p, (0 until p.listaAlternativas().size).shuffled())
                },
                intentoNumero = if (modo == ModoCuestionario.PRUEBA) usados + 1 else 0,
                maxIntentos = max,
                intentosRestantes = (max - usados).coerceAtLeast(0),
                mensaje = bloqueo
            )
        }
    }

    fun seleccionar(posicion: Int) {
        val e = _estado.value
        if (e.fase == Fase.PREGUNTANDO && !e.comprobada) {
            _estado.update { it.copy(seleccion = posicion) }
        }
    }

    /** Botón principal: Responder / Siguiente / Ver resultado / Entregar prueba. */
    fun accionPrincipal() {
        val e = _estado.value
        val item = e.items.getOrNull(e.indice) ?: return
        val posicion = e.seleccion ?: return
        val original = item.orden[posicion]
        val esUltima = e.indice == e.items.lastIndex

        // Práctica: primero se muestra la retroalimentación y se guarda la respuesta
        if (modo.feedbackInmediato && !e.comprobada) {
            _estado.update {
                it.copy(comprobada = true, elegidas = it.elegidas + (item.pregunta.id to original))
            }
            guardarRespuestaDePractica(item.pregunta, original)
            return
        }

        val elegidas = e.elegidas + (item.pregunta.id to original)
        when {
            !esUltima -> _estado.update {
                it.copy(elegidas = elegidas, indice = it.indice + 1, seleccion = null, comprobada = false)
            }
            modo.feedbackInmediato -> _estado.update { it.copy(elegidas = elegidas, fase = Fase.RESULTADO) }
            else -> entregarPrueba(elegidas)
        }
    }

    private fun guardarRespuestaDePractica(pregunta: Pregunta, original: Int) {
        viewModelScope.launch {
            db.progresoDao().insertarRespuestas(
                listOf(
                    Respuesta(
                        usuarioId = usuarioId,
                        preguntaId = pregunta.id,
                        indiceElegido = original,
                        esCorrecta = original == pregunta.indiceCorrecta,
                        numeroIntento = 0,
                        fecha = System.currentTimeMillis()
                    )
                )
            )
        }
    }

    private fun entregarPrueba(elegidas: Map<Int, Int>) {
        if (guardando) return
        guardando = true
        val items = _estado.value.items

        viewModelScope.launch {
            try {
                val progreso = db.progresoDao()
                val numero = progreso.intentosUsados(usuarioId, oaCodigo) + 1
                val ahora = System.currentTimeMillis()

                val respuestas = items.map {
                    val elegida = elegidas[it.pregunta.id] ?: -1
                    Respuesta(
                        usuarioId = usuarioId,
                        preguntaId = it.pregunta.id,
                        indiceElegido = elegida,
                        esCorrecta = elegida == it.pregunta.indiceCorrecta,
                        numeroIntento = numero,
                        fecha = ahora
                    )
                }
                val correctas = respuestas.count { it.esCorrecta }
                val total = items.size
                val porcentaje = ReglaInforme.porcentaje(correctas, total)

                db.withTransaction {
                    progreso.insertarRespuestas(respuestas)
                    progreso.insertarIntento(
                        Intento(
                            usuarioId = usuarioId, oaCodigo = oaCodigo, numero = numero,
                            correctas = correctas, total = total, fecha = ahora
                        )
                    )
                    progreso.insertarInforme(
                        Informe(
                            usuarioId = usuarioId, oaCodigo = oaCodigo, intentoNumero = numero,
                            correctas = correctas, total = total, porcentaje = porcentaje,
                            clasificacion = ReglaInforme.clasificar(porcentaje),
                            fecha = ahora, estado = EstadoInforme.PENDIENTE_VALIDACION
                        )
                    )
                }

                _estado.update {
                    it.copy(
                        elegidas = elegidas,
                        fase = Fase.RESULTADO,
                        intentoNumero = numero,
                        intentosRestantes = (it.maxIntentos - numero).coerceAtLeast(0)
                    )
                }
            } finally {
                guardando = false
            }
        }
    }

    fun revisar() = _estado.update { it.copy(fase = Fase.REVISION, indiceRevision = 0) }

    fun volverAlResultado() = _estado.update { it.copy(fase = Fase.RESULTADO) }

    fun revisionSiguiente() = _estado.update {
        if (it.indiceRevision < it.items.lastIndex) it.copy(indiceRevision = it.indiceRevision + 1) else it
    }

    fun revisionAnterior() = _estado.update {
        if (it.indiceRevision > 0) it.copy(indiceRevision = it.indiceRevision - 1) else it
    }

    fun repetir() = iniciar(usuarioId, oaCodigo, modo)
}