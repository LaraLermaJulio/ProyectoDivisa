package com.example.divisa.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.util.Log
import com.example.divisa.DivisaApplication
import com.example.divisa.data.DefaultAppContainer
import com.example.divisa.data.DivisaDao

class DivisaContentProvider : ContentProvider() {

    companion object {
        private const val AUTHORITY = "com.example.divisa.provider"
        private const val DIVISAS = 1
        private const val DIVISA_ID = 2

        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/divisas")

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, "divisas", DIVISAS)
            addURI(AUTHORITY, "divisas/*", DIVISA_ID)
        }
    }

    private lateinit var divisaDao: DivisaDao

    override fun onCreate(): Boolean {
        context?.applicationContext?.let { appContext ->
            val app = appContext as DivisaApplication
            divisaDao = (app.container as DefaultAppContainer).database.divisaDao()
        } ?: return false
        return true
    }



    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        Log.d("DivisaContentProvider", "Query recibida: ${uri.toString()}, selection: $selection, args: ${selectionArgs?.joinToString()}") // ✅ Verifica la consulta

        return when (uriMatcher.match(uri)) {
            DIVISAS -> {
                val fecha = selectionArgs?.getOrNull(0) ?: ""
                Log.d("DivisaContentProvider", "Buscando divisas con fecha: $fecha") // ✅ Verifica filtro de fecha
                divisaDao.obtenerDivisasPorFechaCursor(fecha)
            }
            else -> null
        }
    }


    override fun getType(uri: Uri): String? {
        return when (uriMatcher.match(uri)) {
            DIVISAS -> "vnd.android.cursor.dir/vnd.com.example.divisa.provider.divisas"
            DIVISA_ID -> "vnd.android.cursor.item/vnd.com.example.divisa.provider.divisas"
            else -> null
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        // No implementado en este ejemplo
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        // No implementado en este ejemplo
        return 0
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        // No implementado en este ejemplo
        return 0
    }
}