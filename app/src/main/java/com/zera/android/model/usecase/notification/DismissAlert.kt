package com.zera.android.model.usecase.notification

import com.zera.android.model.remote.client.AdmCoreClient
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class DismissAlert(
    private val closeAlert: suspend (String) -> Unit = { alertId ->
        AdmCoreClient.notificationsService.closeAlert(alertId)
    },
) {
    /**
     * Tira o alerta das listas na hora e o fecha na API. Em falha, ele volta às listas e a exceção
     * é relançada. `404` conta como sucesso: o alerta não existe ou é de outro usuário, então não
     * deve voltar. Roda em [NonCancellable] para sair da tela não cancelar o fechamento.
     */
    suspend fun execute(alertId: String) {
        DismissedAlerts.add(alertId)
        withContext(NonCancellable) {
            try {
                closeAlert(alertId)
            } catch (e: HttpException) {
                if (e.code() != NOT_FOUND) {
                    DismissedAlerts.remove(alertId)
                    throw e
                }
            } catch (e: Exception) {
                DismissedAlerts.remove(alertId)
                throw e
            }
        }
    }

    private companion object {
        const val NOT_FOUND = 404
    }
}
