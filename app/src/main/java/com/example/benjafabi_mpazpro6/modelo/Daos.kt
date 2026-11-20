package com.example.benjafabi_mpazpro6.modelo

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.benjafabi_mpazpro6.modelo.EstadoContenido

@Dao
interface CurricularDao {

    @Query("""
        SELECT o.* FROM objetivos o
        INNER JOIN asignaciones a ON a.oaCodigo = o.codigo
        WHERE a.curso = :curso AND o.estado = 'PUBLICADO'
        ORDER BY o.asignatura
    """)
    suspend fun oaAsignadosPublicados(curso: String): List<ObjetivoAprendizaje>

    @Query("SELECT * FROM objetivos WHERE codigo = :codigo LIMIT 1")
    suspend fun obtenerOa(codigo: String): ObjetivoAprendizaje?

    @Query("SELECT * FROM objetivos ORDER BY asignatura, codigo")
    suspend fun todosLosOa(): List<ObjetivoAprendizaje>

    @Query("UPDATE objetivos SET estado = :nuevoEstado WHERE codigo = :oaCodigo")
    suspend fun actualizarEstadoOa(oaCodigo: String, nuevoEstado: EstadoContenido)

    @Query("SELECT * FROM contenidos WHERE oaCodigo = :oaCodigo")
    suspend fun contenidosDe(oaCodigo: String): List<Contenido>

    @Query("SELECT * FROM preguntas WHERE oaCodigo = :oaCodigo AND tipo = :tipo ORDER BY dificultad, id")
    suspend fun preguntasDe(oaCodigo: String, tipo: TipoPregunta): List<Pregunta>

    @Query("SELECT * FROM configs_prueba WHERE oaCodigo = :oaCodigo LIMIT 1")
    suspend fun configDe(oaCodigo: String): ConfigPrueba?

    @Query("UPDATE configs_prueba SET estado = :nuevoEstado WHERE oaCodigo = :oaCodigo")
    suspend fun actualizarEstadoConfigPrueba(oaCodigo: String, nuevoEstado: EstadoContenido)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarOas(lista: List<ObjetivoAprendizaje>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarContenidos(lista: List<Contenido>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarPreguntas(lista: List<Pregunta>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarAsignaciones(lista: List<Asignacion>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarConfig(config: ConfigPrueba)
}

@Dao
interface ProgresoDao {

    @Query("""
        SELECT DISTINCT r.preguntaId FROM respuestas r
        INNER JOIN preguntas p ON p.id = r.preguntaId
        WHERE r.usuarioId = :usuarioId AND p.oaCodigo = :oaCodigo AND p.tipo = :tipo
    """)
    suspend fun preguntasRespondidas(usuarioId: String, oaCodigo: String, tipo: TipoPregunta): List<Int>

    @Query("SELECT * FROM intentos WHERE usuarioId = :usuarioId AND oaCodigo = :oaCodigo ORDER BY numero DESC LIMIT 1")
    suspend fun ultimoIntento(usuarioId: String, oaCodigo: String): Intento?

    @Insert
    suspend fun insertarInforme(informe: Informe)

    @Insert
    suspend fun insertarRespuestas(lista: List<Respuesta>)

    @Insert
    suspend fun insertarIntento(intento: Intento)

    @Query("SELECT COUNT(*) FROM intentos WHERE usuarioId = :usuarioId AND oaCodigo = :oaCodigo")
    suspend fun intentosUsados(usuarioId: String, oaCodigo: String): Int

    @Query("""
        SELECT DISTINCT r.preguntaId FROM respuestas r
        INNER JOIN preguntas p ON p.id = r.preguntaId
        WHERE r.usuarioId = :usuarioId AND p.tipo IN ('ACTIVIDAD', 'DESAFIO')
    """)
    suspend fun preguntasPracticadas(usuarioId: String): List<Int>

    @Query("""
        SELECT COUNT(*) FROM preguntas p
        INNER JOIN asignaciones a ON a.oaCodigo = p.oaCodigo
        INNER JOIN objetivos o ON o.codigo = p.oaCodigo
        WHERE a.curso = :curso AND o.estado = 'PUBLICADO'
        AND p.tipo IN ('ACTIVIDAD', 'DESAFIO')
    """)
    suspend fun actividadesTotales(curso: String): Int

    @Query("""
        SELECT * FROM respuestas
        WHERE usuarioId = :usuarioId AND numeroIntento = :numero
        AND preguntaId IN (SELECT id FROM preguntas WHERE oaCodigo = :oaCodigo AND tipo = 'PRUEBA_FINAL')
    """)
    suspend fun respuestasDeIntento(usuarioId: String, oaCodigo: String, numero: Int): List<Respuesta>
}