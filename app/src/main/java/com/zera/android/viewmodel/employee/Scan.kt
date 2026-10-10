package com.zera.android.viewmodel.employee

import android.os.SystemClock
import androidx.compose.runtime.mutableStateOf
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.viewmodel.ZeraViewModel

/** Tempo em que uma nova leitura do mesmo código é ignorada. */
private const val REPEATED_SCAN_WINDOW_MS = 2_000L

data class ScanState(
    val hasCameraPermission: Boolean = false,
    val scannedCode: String? = null,
)

/**
 * ViewModel da tela de escanear item do operário.
 *
 * Guarda se o app tem a permissão de câmera. A checagem e o pedido da permissão acontecem
 * na tela (precisam de Context/Activity); o resultado chega aqui por [onCameraPermissionChanged].
 *
 * Os códigos lidos pela câmera chegam em [onBarcodeDetected], que ignora as leituras
 * repetidas do mesmo código (a câmera manda o mesmo código a cada frame).
 *
 * @param now relógio em milissegundos; trocado nos testes.
 */
class ScanViewModel(
    private val now: () -> Long = SystemClock::elapsedRealtime,
) : ZeraViewModel() {
    private val _state = mutableStateOf(ScanState())
    val state = _state

    private var lastScanAt = 0L

    fun onCameraPermissionChanged(granted: Boolean) {
        _state.value = _state.value.copy(hasCameraPermission = granted)
    }

    /**
     * Recebe um código lido pela câmera.
     *
     * O mesmo código é ignorado enquanto continuar aparecendo na câmera; ele só é aceito de
     * novo depois de ficar [REPEATED_SCAN_WINDOW_MS] sem ser lido.
     *
     * @return `true` se a leitura foi aceita; `false` se foi ignorada por ser repetida.
     */
    fun onBarcodeDetected(code: String): Boolean {
        val time = now()
        val isRepeated = code == _state.value.scannedCode &&
            time - lastScanAt < REPEATED_SCAN_WINDOW_MS
        lastScanAt = time
        if (isRepeated) return false

        _state.value = _state.value.copy(scannedCode = code)
        // TODO: pesquisar o produto pelo código — encontrado: detalhes do item; senão: cadastro.
        return true
    }

    fun onRegisterWithoutLabelClick() {
        ZeraNavigator.push(Route.ManualRegister())
    }

    fun onRegisterModelClick() {
        // TODO: navegar para o cadastro de modelo do operário
    }
}
