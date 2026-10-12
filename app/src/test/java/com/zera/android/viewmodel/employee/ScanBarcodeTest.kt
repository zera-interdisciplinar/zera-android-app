package com.zera.android.viewmodel.employee

import com.zera.android.model.dto.inventory.ItemResponseDTO
import com.zera.android.model.usecase.inventory.GetItemByBarcode
import com.zera.android.view.navigation.NavCommand
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class ScanBarcodeTest {
    private var time = 10_000L

    @Before
    fun drainPendingNavCommands() = runBlocking {
        while (withTimeoutOrNull(50) { ZeraNavigator.commands.first() } != null) {
            // esvazia o canal compartilhado entre testes
        }
    }

    @Test
    fun startsWithoutScannedCode() {
        val viewModel = scanViewModel()
        assertNull(viewModel.state.value.scannedCode)
    }

    @Test
    fun firstReadIsAccepted() {
        val viewModel = scanViewModel(found = null)
        assertTrue(viewModel.onBarcodeDetected("1-2-3"))
        assertEquals("1-2-3", viewModel.state.value.scannedCode)
    }

    @Test
    fun sameCodeIsIgnoredWhileStillInFrame() {
        val viewModel = scanViewModel(found = null)
        viewModel.onBarcodeDetected("1-2-3")

        repeat(10) {
            time += 500
            assertFalse(viewModel.onBarcodeDetected("1-2-3"))
        }
    }

    @Test
    fun sameCodeIsAcceptedAgainAfterLeavingFrame() {
        val viewModel = scanViewModel(found = null)
        viewModel.onBarcodeDetected("1-2-3")

        time += 2_000
        assertTrue(viewModel.onBarcodeDetected("1-2-3"))
    }

    @Test
    fun differentCodeIsAcceptedRightAway() {
        val viewModel = scanViewModel(found = null)
        viewModel.onBarcodeDetected("1-2-3")

        time += 100
        assertTrue(viewModel.onBarcodeDetected("4-5-6"))
        assertEquals("4-5-6", viewModel.state.value.scannedCode)
    }

    @Test
    fun foundItemOpensDetails() = runBlocking {
        val viewModel = scanViewModel(
            found = ItemResponseDTO(id = "item-9", name = "Notebook", barcode = "1-2-3"),
        )
        viewModel.onBarcodeDetected("1-2-3")

        val command = nextNavCommand()
        assertEquals(Route.ItemDetails(itemId = "item-9"), (command as NavCommand.Navigate).route)
    }

    @Test
    fun missingItemOpensRegisterWithBarcode() = runBlocking {
        val viewModel = scanViewModel(found = null)
        viewModel.onBarcodeDetected("1-2-3")

        val command = nextNavCommand()
        assertEquals(Route.ManualRegister(barcode = "1-2-3"), (command as NavCommand.Navigate).route)
    }

    @Test
    fun lookupErrorStaysOnScan() {
        val viewModel = scanViewModel(
            getItemByBarcode = GetItemByBarcode { throw httpError(500) },
        )
        viewModel.onBarcodeDetected("1-2-3")

        assertFalse(viewModel.state.value.isLookingUp)
        assertTrue(!viewModel.state.value.lookupError.isNullOrBlank())
    }

    @Test
    fun lookupInFlightIgnoresNewFrames() {
        var pending: (suspend () -> Unit)? = null
        val viewModel = ScanViewModel(
            now = { time },
            getItemByBarcode = GetItemByBarcode {
                ItemResponseDTO(id = "item-1", name = "X")
            },
            launchLookup = { block -> pending = block },
        )

        assertTrue(viewModel.onBarcodeDetected("1-2-3"))
        assertTrue(viewModel.state.value.isLookingUp)
        assertFalse(viewModel.onBarcodeDetected("4-5-6"))
        assertEquals("1-2-3", viewModel.state.value.scannedCode)

        runBlocking { pending!!() }
        assertFalse(viewModel.state.value.isLookingUp)
    }

    private fun scanViewModel(
        found: ItemResponseDTO? = null,
        getItemByBarcode: GetItemByBarcode = GetItemByBarcode {
            found ?: throw httpError(404)
        },
    ) = ScanViewModel(
        now = { time },
        getItemByBarcode = getItemByBarcode,
        launchLookup = { block -> runBlocking { block() } },
    )

    private suspend fun nextNavCommand(): NavCommand =
        withTimeout(1_000) { ZeraNavigator.commands.first() }

    private fun httpError(code: Int): HttpException {
        val body = "{}".toResponseBody("application/json".toMediaType())
        return HttpException(Response.error<String>(code, body))
    }
}
