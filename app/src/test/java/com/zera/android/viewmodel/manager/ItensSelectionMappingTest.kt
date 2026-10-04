package com.zera.android.viewmodel.manager

import com.zera.android.model.dto.inventory.CategoryResponseDTO
import com.zera.android.model.dto.inventory.ItemResponseDTO
import com.zera.android.model.dto.inventory.ModelResponseDTO
import com.zera.android.view.components.inputs.SelectOption
import org.junit.Assert.assertEquals
import org.junit.Test

class ItensSelectionMappingTest {
    @Test
    fun mapsStockItemToSelectOption() {
        val option = ItensSelectionViewModel.optionFrom(
            ItemResponseDTO(
                id = "9c858901-8a57-4791-81fe-4c455b099bc9",
                name = "Notebook Dell Latitude",
                displayCode = "IT-12",
                status = "IN_STOCK",
                model = ModelResponseDTO(
                    name = "Latitude",
                    category = CategoryResponseDTO(id = "c1", name = "Informática"),
                ),
            ),
        )

        assertEquals("Notebook Dell Latitude", option.name)
        assertEquals("9c858901-8a57-4791-81fe-4c455b099bc9", option.value)
        assertEquals("ID IT-12 · Informática", option.description)
    }

    @Test
    fun fallsBackToIdWhenDisplayCodeAndCategoryAreMissing() {
        val option = ItensSelectionViewModel.optionFrom(
            ItemResponseDTO(id = "id-1", name = "Monitor", status = "IN_STOCK"),
        )

        assertEquals("ID id-1", option.description)
    }

    @Test
    fun appendSkipsDuplicateValues() {
        val current = listOf(SelectOption(name = "A", value = "1"))
        val incoming = listOf(
            ItemResponseDTO(id = "1", name = "A de novo"),
            ItemResponseDTO(id = "2", name = "B"),
        )

        val result = ItensSelectionViewModel.appendOptions(current, incoming)

        assertEquals(listOf("1", "2"), result.map { it.value })
        assertEquals("A", result.first().name)
    }
}
