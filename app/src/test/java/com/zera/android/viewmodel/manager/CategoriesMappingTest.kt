package com.zera.android.viewmodel.manager

import com.zera.android.model.dto.inventory.CategoryResponseDTO
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CategoriesMappingTest {
    @Test
    fun mapsCategoryToOption() {
        val option = CategoriesViewModel.optionFrom(
            CategoryResponseDTO(id = "c1", name = "Informática", description = "Notebooks e monitores"),
        )

        assertEquals("Informática", option.name)
        assertEquals("Notebooks e monitores", option.description)
    }

    @Test
    fun singularAndPluralLabel() {
        assertEquals("1 Categoria", CategoriesViewModel.totalCategoriesLabel(1))
        assertEquals("12 Categorias", CategoriesViewModel.totalCategoriesLabel(12))
    }

    @Test
    fun searchMatchesNameOrDescription() {
        val categories = listOf(
            CategoryResponseDTO(id = "c1", name = "Informática", description = null),
            CategoryResponseDTO(id = "c2", name = "Mobiliário", description = "Cadeiras"),
        )

        assertEquals(
            listOf("c2"),
            CategoriesViewModel.visibleCategories(categories, "cadeira").map { it.id },
        )
        assertEquals(categories, CategoriesViewModel.visibleCategories(categories, " "))
    }

    @Test
    fun rejectsIncompleteCreateForm() {
        assertEquals(
            "Informe o nome da categoria.",
            CategoryCreationViewModel.validationError(" "),
        )
        assertNull(CategoryCreationViewModel.validationError("Informática"))
    }
}
