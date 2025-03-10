package com.example.divisa.ui.screens

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import com.example.divisa.data.DivisaDatabase
import com.example.divisa.model.Divisa
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Locale

class DivisaContentProvider : ContentProvider() {

    companion object {
        const val AUTHORITY = "com.example.divisa.provider"
        const val TABLE_NAME = "divisas"
        const val COLUMN_ID = "id"
        const val COLUMN_MONEDA = "moneda"
        const val COLUMN_TASA = "tasa"
        const val COLUMN_FECHAHORA = "fechaHora"
    }

    private lateinit var database: DivisaDatabase

    override fun onCreate(): Boolean {
        context?.let { ctx ->
            database = DivisaDatabase.getInstance(ctx)
            runBlocking {
                // Insertar datos de ejemplo si la tabla está vacía
                if (database.divisaDao().obtenerDivisasPorRango("USD", "0000-01-01 00:00:00", "9999-12-31 23:59:59").isEmpty()) {
                    insertarDatosEjemplo()
                }
            }
        }
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val currency = uri.getQueryParameter("currency") ?: return null
        val startDate = uri.getQueryParameter("startDate") ?: return null
        val endDate = uri.getQueryParameter("endDate") ?: return null

        val listaDivisas: List<Divisa> = runBlocking {
            database.divisaDao().obtenerDivisasPorRango(currency, startDate, endDate)
        }

        val cursor = MatrixCursor(arrayOf(COLUMN_ID, COLUMN_MONEDA, COLUMN_TASA, COLUMN_FECHAHORA))
        listaDivisas.forEach { divisa ->
            cursor.addRow(arrayOf(divisa.id, divisa.moneda, divisa.tasa, divisa.fechaHora))
        }
        return cursor
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int = 0
    override fun getType(uri: Uri): String? = null

    private suspend fun insertarDatosEjemplo() {
        val dao = database.divisaDao()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val ahora = System.currentTimeMillis()

        val listaEjemplo = listOf(
            Divisa(moneda = "USD", tasa = 18.50, fechaHora = sdf.format(ahora - 3600000L)),
            Divisa(moneda = "USD", tasa = 18.60, fechaHora = sdf.format(ahora)),
            Divisa(moneda = "USD", tasa = 18.55, fechaHora = sdf.format(ahora + 3600000L)),
            Divisa(moneda = "EUR", tasa = 19.90, fechaHora = sdf.format(ahora))
        )
        dao.insertarDivisas(listaEjemplo)
    }
}
