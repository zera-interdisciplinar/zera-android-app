package com.zera.android.viewmodel.main.registration

import android.os.SystemClock
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.zera.android.model.usecase.inventory.GetItemByBarcode
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.viewmodel.ZeraViewModel
import com.zera.android.viewmodel.errorDescription
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

/** Tempo em que uma nova leitura do mesmo código é ignorada. */
private const val REPEATED_SCAN_WINDOW_MS = 2_000L

data class ScanState(
    val hasCameraPermission: Boolean = false,
    val scannedCode: String? = null,
    val isLookingUp: Boolean = false,
    val lookupError: String? = null,
)

/**
 * ViewModel da tela de escanear item do operário.
 *
 * Guarda se o app tem a permissão de câmera. A checagem e o pedido da permissão acontecem
 * na tela (precisam de Context/Activity); o resultado chega aqui por [onCameraPermissionChanged].
 *
 * Os códigos lidos pela câmera chegam em [onBarcodeDetected], que ignora as leituras
 * repetidas do mesmo código (a câmera manda o mesmo código a cada frame) e busca o item
 * em [GetItemByBarcode].
 *
 * @param now relógio em milissegundos; trocado nos testes.
 * @param launchLookup dispara a busca; nos testes roda na hora, sem o dispatcher Main.
 */
class ScanViewModel(
    private val now: () -> Long = SystemClock::elapsedRealtime,
    private val getItemByBarcode: GetItemByBarcode = GetItemByBarcode(),
    private val launchLookup: ((suspend () -> Unit) -> Unit)? = null,
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
     * novo depois de ficar [REPEATED_SCAN_WINDOW_MS] sem ser lido. Enquanto a busca está
     * em andamento, qualquer leitura nova é ignorada.
     *
     * @return `true` se a leitura foi aceita; `false` se foi ignorada por ser repetida.
     */
    fun onBarcodeDetected(code: String): Boolean {
        if (_state.value.isLookingUp) return false

        val time = now()
        val isRepeated = code == _state.value.scannedCode &&
            time - lastScanAt < REPEATED_SCAN_WINDOW_MS
        lastScanAt = time
        if (isRepeated) return false

        _state.value = _state.value.copy(
            scannedCode = code,
            isLookingUp = true,
            lookupError = null,
        )
        startLookup { lookupItem(code) }
        return true
    }

    private fun startLookup(block: suspend () -> Unit) {
        val launcher = launchLookup
        if (launcher != null) launcher(block) else viewModelScope.launch { block() }
    }

    private suspend fun lookupItem(code: String) {
        try {
            val item = getItemByBarcode.execute(code)
            _state.value = _state.value.copy(isLookingUp = false)
            if (item != null) {
                ZeraNavigator.push(Route.ItemDetails(itemId = item.id))
            } else {
                ZeraNavigator.push(Route.ManualRegister(barcode = code))
            }
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            _state.value = _state.value.copy(
                isLookingUp = false,
                lookupError = errorDescription(error, "Não conseguimos buscar o item. Tente novamente."),
            )
        }
    }

    fun onErrorDismiss() {
        _state.value = _state.value.copy(lookupError = null)
    }

    fun onRegisterWithoutLabelClick() {
        ZeraNavigator.push(Route.ManualRegister())
    }

    fun onRegisterModelClick() {
        ZeraNavigator.push(Route.Models)
    }
}
