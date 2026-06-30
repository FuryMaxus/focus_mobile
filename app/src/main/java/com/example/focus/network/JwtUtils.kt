package com.example.focus.network

import android.util.Base64
import org.json.JSONObject

object JwtUtils {
    /**
     * Decodifica el payload de un JWT y extrae el valor de una clave (ej: "role").
     * Un JWT tiene el formato: header.payload.signature
     */
    fun extractClaim(token: String, claim: String): String? {
        return try {
            val parts = token.split(".")
            if (parts.size < 2) return null
            
            val payloadBase64 = parts[1]
            val decodedBytes = Base64.decode(payloadBase64, Base64.URL_SAFE)
            val payloadString = String(decodedBytes, Charsets.UTF_8)
            
            val jsonObject = JSONObject(payloadString)
            // En el backend envías user.role.value, solemos buscar "role" o el nombre que definas
            if (jsonObject.has(claim)) {
                jsonObject.getString(claim)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
