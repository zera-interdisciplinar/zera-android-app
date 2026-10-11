package com.zera.android.viewmodel.employee

import org.junit.Assert.assertEquals
import org.junit.Test

class ManualRegisterViewModelTest {
    @Test
    fun startsWithBarcodeWhenProvided() {
        val viewModel = ManualRegisterViewModel(initialBarcode = "7891234567890")
        assertEquals("7891234567890", viewModel.state.value.barcode)
    }

    @Test
    fun startsWithoutBarcodeWhenOmitted() {
        val viewModel = ManualRegisterViewModel()
        assertEquals("", viewModel.state.value.barcode)
    }
}
