package com.example.benjafabi_mpazpro6.modelo

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "usuarios",
    indices = [Index(value = ["nombreUsuario"], unique = true)]
)
data class Usuario(
    @PrimaryKey val id: String,
    val nombreUsuario: String,
    val clave: String,
    val nombre: String,
    val rol: Rol,
    val curso: String? = null   // solo se usa para los estudiantes
)