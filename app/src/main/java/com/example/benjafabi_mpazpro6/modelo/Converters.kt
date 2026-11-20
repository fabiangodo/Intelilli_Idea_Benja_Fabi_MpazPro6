package com.example.benjafabi_mpazpro6.modelo

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun deRolATexto(rol: Rol): String {
        return rol.name
    }

    @TypeConverter
    fun deTextoARol(valor: String): Rol {
        return Rol.valueOf(valor)
    }

}