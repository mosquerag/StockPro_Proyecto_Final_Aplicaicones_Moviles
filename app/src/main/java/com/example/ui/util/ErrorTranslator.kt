package com.example.ui.util

import android.content.Context
import com.example.R

/**
 * Traduce códigos de error estables provenientes del backend Spring Boot
 * a cadenas de texto localizadas (español e inglés) definidas en strings.xml.
 */
object ErrorTranslator {

    fun getErrorMessageRes(codigo: String?): Int {
        return when (codigo?.trim()?.uppercase()) {
            "STOCK_INSUFICIENTE" -> R.string.error_stock_insuficiente
            "CREDENCIALES_INVALIDAS" -> R.string.error_credenciales_invalidas
            "TOKEN_EXPIRADO" -> R.string.error_token_expirado
            "TOKEN_INVALIDO" -> R.string.error_token_invalido
            "TOKEN_INVALIDO_O_EXPIRADO" -> R.string.error_token_expirado
            "ACCESO_DENEGADO" -> R.string.error_acceso_denegado
            "PRODUCTO_NO_ENCONTRADO" -> R.string.error_producto_no_encontrado
            "LOTE_NO_ENCONTRADO" -> R.string.error_lote_no_encontrado
            "NEGOCIO_NO_ENCONTRADO" -> R.string.error_negocio_no_encontrado
            "ENTRADA_NO_ENCONTRADA" -> R.string.error_entrada_no_encontrada
            "VENTA_NO_ENCONTRADA" -> R.string.error_venta_no_encontrada
            "ENTRADA_YA_RECIBIDA" -> R.string.error_entrada_ya_recibida
            "EMAIL_DUPLICADO", "VALOR_DUPLICADO" -> R.string.error_email_duplicado
            "PRODUCTO_INACTIVO" -> R.string.error_producto_inactivo
            "VALIDACION_FALLIDA" -> R.string.error_validacion_fallida
            "OPERACION_INVALIDA" -> R.string.error_operacion_invalida
            "RECURSO_NO_ENCONTRADO", "SOLICITUD_INVALIDA" -> R.string.error_recurso_no_encontrado
            "ERROR_INTERNO" -> R.string.error_interno
            "ERROR_CONEXION" -> R.string.error_conexion_servidor
            else -> R.string.error_generic
        }
    }

    fun translate(context: Context, codigo: String?, fallbackMensaje: String? = null): String {
        val stringRes = getErrorMessageRes(codigo)
        return if (stringRes != R.string.error_generic) {
            context.getString(stringRes)
        } else {
            fallbackMensaje?.takeIf { it.isNotBlank() } ?: context.getString(R.string.error_generic)
        }
    }
}
