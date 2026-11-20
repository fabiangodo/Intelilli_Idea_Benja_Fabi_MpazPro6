package com.example.benjafabi_mpazpro6

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidadoresTest {

    private fun validarIntentos(input: String): Pair<Boolean, String?> {
        if (input.isBlank()) return Pair(false, "Campo obligatorio")
        val num = input.toIntOrNull()
        if (num == null || num !in 1..5) return Pair(false, "Debe ser un número entero entre 1 y 5")
        return Pair(true, null)
    }

    @Test
    fun validarIntentos_campoVacio_retornaError() {
        val (esValido, error) = validarIntentos("")
        assertFalse(esValido)
        assertEquals("Campo obligatorio", error)
    }

    @Test
    fun validarIntentos_numeroInvalido_retornaError() {
        val (esValido, error) = validarIntentos("7")
        assertFalse(esValido)
        assertEquals("Debe ser un número entero entre 1 y 5", error)
    }

    @Test
    fun validarIntentos_correctos_retornaExito() {
        val (esValido, error) = validarIntentos("3")
        assertTrue(esValido)
        assertEquals(null, error)
    }
}