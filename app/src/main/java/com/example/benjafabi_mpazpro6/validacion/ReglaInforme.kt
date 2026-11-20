package com.example.benjafabi_mpazpro6.validacion

import com.example.benjafabi_mpazpro6.modelo.Clasificacion

/**
 * Regla del informe pedagógico (sin IA, solo porcentajes).
 * 70 % o más de aciertos en un OA = FORTALEZA; menos de 70 % = REFUERZO.
 * La app NO diagnostica ni toma decisiones académicas: el docente valida el informe.
 */
object ReglaInforme {
    const val UMBRAL_FORTALEZA = 70

    fun porcentaje(correctas: Int, total: Int): Int =
        if (total == 0) 0 else correctas * 100 / total

    fun clasificar(porcentaje: Int): Clasificacion =
        if (porcentaje >= UMBRAL_FORTALEZA) Clasificacion.FORTALEZA else Clasificacion.REFUERZO
}
