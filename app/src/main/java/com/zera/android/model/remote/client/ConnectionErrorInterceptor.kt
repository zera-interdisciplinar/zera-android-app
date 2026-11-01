package com.zera.android.model.remote.client

import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Avisa [onConnectionLost] quando uma chamada falha por falta de conexão (ver [isConnectionError])
 * e relança a exceção, para o `catch` de cada tela continuar rodando. O [gate] garante um aviso só
 * enquanto a tela de conexão estiver aberta, mesmo com várias chamadas falhando juntas.
 */
class ConnectionErrorInterceptor(
    private val gate: ConnectionErrorGate,
    private val onConnectionLost: () -> Unit,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response =
        try {
            chain.proceed(chain.request())
        } catch (error: IOException) {
            if (error.isConnectionError() && gate.tryOpen()) onConnectionLost()
            throw error
        }
}

/**
 * Estado da tela de conexão aberta pelo interceptor.
 *
 * - [tryOpen] só deixa abrir uma tela por vez; [release] libera ao sair dela.
 * - [suppress] desliga o aviso enquanto a Splash roda o boot e o restore: ela mesma navega para a
 *   tela de conexão (com `fromBoot = true`), e o interceptor abriria uma segunda.
 */
class ConnectionErrorGate {
    private val open = AtomicBoolean(false)
    private val suppressed = AtomicBoolean(false)

    fun tryOpen(): Boolean = !suppressed.get() && open.compareAndSet(false, true)

    fun release() {
        open.set(false)
    }

    fun suppress(enabled: Boolean) {
        suppressed.set(enabled)
    }
}

object ConnectionLoss {
    val gate = ConnectionErrorGate()

    val interceptor: Interceptor = ConnectionErrorInterceptor(
        gate = gate,
        onConnectionLost = {
            // Um carregamento pode ter bloqueado a navegação; sem liberar, o push seria ignorado.
            ZeraNavigator.navigationStatus(true)
            ZeraNavigator.push(Route.ConnectionError())
        },
    )
}
