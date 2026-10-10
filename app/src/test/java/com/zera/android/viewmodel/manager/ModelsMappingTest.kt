package com.zera.android.viewmodel.manager

import com.zera.android.model.dto.inventory.MaterialResponseDTO
import com.zera.android.model.dto.inventory.ModelResponseDTO
import com.zera.android.view.theme.ZeraColorFamily
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ModelsMappingTest {
    @Test
    fun mapsApprovedModelToOption() {
        val option = ModelsViewModel.optionFrom(
            ModelResponseDTO(
                id = "m1",
                name = "Latitude 5420",
                manufacturer = "Dell",
                materials = listOf(MaterialResponseDTO(code = "PLASTIC", name = "Plástico")),
                approvalStatus = "APPROVED",
            ),
        )

        assertEquals("Latitude 5420", option.name)
        assertEquals("Dell · Plástico", option.description)
        assertEquals("Aprovado", option.statusText)
        assertEquals(ZeraColorFamily.Green, option.statusStyle)
    }

    @Test
    fun singularAndPluralLabel() {
        assertEquals("1 Modelo", ModelsViewModel.totalModelsLabel(1))
        assertEquals("1.230 Modelos", ModelsViewModel.totalModelsLabel(1230))
    }

    @Test
    fun rejectsIncompleteCreateForm() {
        assertEquals(
            "Informe o nome do modelo.",
            ModelCreationViewModel.validationError(" ", "Dell", "c1", "PLASTIC", ""),
        )
        assertEquals(
            "Selecione a categoria.",
            ModelCreationViewModel.validationError("Notebook", "Dell", null, "PLASTIC", ""),
        )
        assertNull(
            ModelCreationViewModel.validationError("Notebook", "Dell", "c1", "PLASTIC", "ok"),
        )
    }

    @Test
    fun appendSkipsDuplicateIds() {
        val current = listOf(ModelResponseDTO(id = "a", name = "A"))
        val incoming = listOf(
            ModelResponseDTO(id = "a", name = "A2"),
            ModelResponseDTO(id = "b", name = "B"),
        )

        assertEquals(listOf("a", "b"), ModelsViewModel.appendModels(current, incoming).map { it.id })
    }
}
