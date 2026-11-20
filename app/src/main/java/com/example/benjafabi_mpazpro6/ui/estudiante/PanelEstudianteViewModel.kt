package com.example.benjafabi_mpazpro6.ui.estudiante

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.benjafabi_mpazpro6.modelo.AppDatabase
import com.example.benjafabi_mpazpro6.modelo.Contenido
import com.example.benjafabi_mpazpro6.modelo.ObjetivoAprendizaje
import com.example.benjafabi_mpazpro6.modelo.TipoPregunta
import com.example.benjafabi_mpazpro6.modelo.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Resumen de una unidad (un OA) para el estudiante: contenido, progreso y estado de la prueba final. */
data class UnidadUi(
    val oa: ObjetivoAprendizaje,
    val contenido: Contenido?,
    val actividadesHechas: Int,
    val actividadesTotal: Int,
    val desafiosHechos: Int,
    val desafiosTotal: Int,
    val maxIntentos: Int,
    val habilitada: Boolean,
    val intentosUsados: Int,
    val ultimoCorrectas: Int?,
    val ultimoTotal: Int?
) {
    val intentosRestantes: Int get() = (maxIntentos - intentosUsados).coerceAtLeast(0)
    val hechas: Int get() = actividadesHechas + desafiosHechos
    val total: Int get() = actividadesTotal + desafiosTotal
    val progreso: Float get() = if (total == 0) 0f else hechas.toFloat() / total
}

data class PanelEstudianteUiState(
    val usuario: Usuario? = null,
    val unidades: List<UnidadUi> = emptyList(),
    val completadas: Int = 0,
    val totales: Int = 0,
    val pruebaDetalle: String = "Cargando..."
)

// Orden en que se muestran las asignaturas
private val ORDEN_ASIGNATURAS = listOf("Lenguaje", "Matem", "Ciencias", "Historia")

private fun posicion(asignatura: String): Int {
    val i = ORDEN_ASIGNATURAS.indexOfFirst { asignatura.startsWith(it) }
    return if (i < 0) ORDEN_ASIGNATURAS.size else i
}

class PanelEstudianteViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)

    private val _uiState = MutableStateFlow(PanelEstudianteUiState())
    val uiState: StateFlow<PanelEstudianteUiState> = _uiState.asStateFlow()

    private var usuarioId: String = ""

    fun cargar(id: String) {
        usuarioId = id
        refrescar()
    }

    /** Vuelve a leer la base de datos para actualizar el progreso del estudiante. */
    fun refrescar() {
        val id = usuarioId
        if (id.isEmpty()) return

        viewModelScope.launch {
            val usuario = db.usuarioDao().obtenerPorId(id)
            val curricular = db.curricularDao()
            val progreso = db.progresoDao()

            val oas = curricular
                .oaAsignadosPublicados(usuario?.curso.orEmpty())
                .sortedBy { posicion(it.asignatura) }

            val unidades = oas.map { oa ->
                val config = curricular.configDe(oa.codigo)
                val ultimo = progreso.ultimoIntento(id, oa.codigo)

                val actRespondidas = progreso.preguntasRespondidas(id, oa.codigo, TipoPregunta.ACTIVIDAD)
                val desRespondidas = progreso.preguntasRespondidas(id, oa.codigo, TipoPregunta.DESAFIO)

                UnidadUi(
                    oa = oa,
                    contenido = curricular.contenidosDe(oa.codigo).firstOrNull(),
                    actividadesHechas = actRespondidas.size,
                    actividadesTotal = curricular.preguntasDe(oa.codigo, TipoPregunta.ACTIVIDAD).size,
                    desafiosHechos = desRespondidas.size,
                    desafiosTotal = curricular.preguntasDe(oa.codigo, TipoPregunta.DESAFIO).size,
                    maxIntentos = config?.maxIntentos ?: 0,
                    habilitada = config?.habilitada ?: false,
                    intentosUsados = progreso.intentosUsados(id, oa.codigo),
                    ultimoCorrectas = ultimo?.correctas,
                    ultimoTotal = ultimo?.total
                )
            }

            val detalle = if (unidades.isEmpty()) {
                "Aún no tienes pruebas asignadas"
            } else {
                "Una por asignatura · hasta ${unidades.maxOf { it.maxIntentos }} intentos"
            }

            _uiState.value = PanelEstudianteUiState(
                usuario = usuario,
                unidades = unidades,
                completadas = unidades.sumOf { it.hechas },
                totales = unidades.sumOf { it.total },
                pruebaDetalle = detalle
            )
        }
    }
}