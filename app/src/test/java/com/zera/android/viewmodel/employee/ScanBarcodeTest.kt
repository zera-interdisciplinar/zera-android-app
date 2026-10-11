package com.zera.android.viewmodel.employee

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ScanBarcodeTest {
    private var time = 10_000L
    private val viewModel = ScanViewModel(now = { time })

    @Test
    fun startsWithoutScannedCode() {
        assertNull(viewModel.state.value.scannedCode)
    }

    @Test
    fun firstReadIsAccepted() {
        assertTrue(viewModel.onBarcodeDetected("1-2-3"))
        assertEquals("1-2-3", viewModel.state.value.scannedCode)
    }

    @Test
    fun sameCodeIsIgnoredWhileStillInFrame() {
        viewModel.onBarcodeDetected("1-2-3")

        // A câmera segue vendo o código a cada 500 ms, por bem mais que a janela de 2 s.
        repeat(10) {
            time += 500
            assertFalse(viewModel.onBarcodeDetected("1-2-3"))
        }
    }

    @Test
    fun sameCodeIsAcceptedAgainAfterLeavingFrame() {
        viewModel.onBarcodeDetected("1-2-3")

        time += 2_000
        assertTrue(viewModel.onBarcodeDetected("1-2-3"))
    }

    @Test
    fun differentCodeIsAcceptedRightAway() {
        viewModel.onBarcodeDetected("1-2-3")

        time += 100
        assertTrue(viewModel.onBarcodeDetected("4-5-6"))
        assertEquals("4-5-6", viewModel.state.value.scannedCode)
    }
}
