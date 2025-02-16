package com.example.divisa.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.divisa.DivisaApplication
import com.example.divisa.data.DivisaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class ActualizarDivisasWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {


    private val divisaRepository: DivisaRepository by lazy {
        (applicationContext as DivisaApplication).container.divisaRepository
    }

    override suspend fun doWork(): Result {
        return try {

            withContext(Dispatchers.IO) {
                divisaRepository.sincronizarDivisas()
            }
            Log.d("ActualizarDivisasWorker", "Sincronización exitosa.")
            Result.success()
        } catch (e: HttpException) {
            Log.e("ActualizarDivisasWorker", "HttpException: ${e.response()?.errorBody()?.string()}")
            Result.retry()
        } catch (e: Exception) {
            Log.e("ActualizarDivisasWorker", "Exception: ${e.message}")
            Result.failure()
        }
    }
}
