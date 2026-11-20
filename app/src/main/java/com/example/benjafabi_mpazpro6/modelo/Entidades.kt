package com.example.benjafabi_mpazpro6.modelo

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class EstadoContenido(val mostrarNombre: String) {
    BORRADOR("Borrador"),
    EN_REVISION("En revisión"),
    AUTORIZADO("Autorizado"),
    PUBLICADO("Publicado")
}

enum class TipoPregunta { ACTIVIDAD, DESAFIO, PRUEBA_FINAL }
enum class Clasificacion { FORTALEZA, REFUERZO }
enum class EstadoInforme { GENERADO, PENDIENTE_VALIDACION, VALIDADO }

@Entity(tableName = "objetivos")
data class ObjetivoAprendizaje(
    @PrimaryKey val codigo: String,          // código oficial del OA
    val nivel: String,
    val asignatura: String,
    val unidad: String,
    val eje: String,
    val descripcion: String,
    val textoReferencia: String,
    val estado: EstadoContenido              // UTP lo cambia hasta PUBLICADO
)

@Entity(tableName = "contenidos")
data class Contenido(
    @PrimaryKey val id: Int,
    val oaCodigo: String,
    val titulo: String,
    val explicacion: String,
    val ejemplo: String,
    val instrucciones: String,
    val imagen: String? = null,              // nombre de archivo en res/drawable
    val audio: String? = null,               // opcional
    val origen: String                       // propio / público / autorizado
)

@Entity(tableName = "preguntas")
data class Pregunta(
    @PrimaryKey val id: Int,
    val oaCodigo: String,
    val tipo: TipoPregunta,
    val enunciado: String,
    val alternativas: String,                // separadas por "|"
    val indiceCorrecta: Int,
    val dificultad: Int,                     // 1 a 3
    val retroalimentacion: String = ""
) {
    fun listaAlternativas(): List<String> = alternativas.split("|")
}

@Entity(tableName = "asignaciones")
data class Asignacion(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val oaCodigo: String,
    val curso: String
)

/**
 * Tabla de Configuración de la Prueba por OA
 * Conecta los 3 Roles:
 * 1. Docente asigna maxIntentos y la envía a revisión/autorización.
 * 2. Jefe UTP cambia el 'estado' a AUTORIZADO o PUBLICADO.
 * 3. Estudiante lee 'maxIntentos' solo cuando 'estado == PUBLICADO' o 'AUTORIZADO' y 'habilitada == true'.
 */
@Entity(tableName = "configs_prueba")
data class ConfigPrueba(
    @PrimaryKey val oaCodigo: String,
    val maxIntentos: Int,
    val habilitada: Boolean,                 // El docente la habilita
    val estado: EstadoContenido = EstadoContenido.EN_REVISION // UTP autoriza/publica
)

@Entity(tableName = "respuestas")
data class Respuesta(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val usuarioId: String,
    val preguntaId: Int,
    val indiceElegido: Int,
    val esCorrecta: Boolean,
    val numeroIntento: Int,                  // 0 en actividades y desafíos; 1, 2, 3... en la prueba
    val fecha: Long
)

@Entity(tableName = "intentos")
data class Intento(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val usuarioId: String,
    val oaCodigo: String,
    val numero: Int,
    val correctas: Int,
    val total: Int,
    val fecha: Long
)

@Entity(tableName = "informes")
data class Informe(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val usuarioId: String,
    val oaCodigo: String,
    val intentoNumero: Int,
    val correctas: Int,
    val total: Int,
    val porcentaje: Int,
    val clasificacion: Clasificacion,
    val fecha: Long,
    val docenteId: String? = null,
    val estado: EstadoInforme
)