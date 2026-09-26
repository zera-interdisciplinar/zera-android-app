package com.zera.android.viewmodel.manager

import com.zera.android.model.dto.inventory.CategoryResponseDTO
import com.zera.android.model.dto.inventory.ItemResponseDTO
import com.zera.android.model.dto.inventory.PagedItemsDTO
import com.zera.android.view.components.outros.ItemStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ItensMappingTest {
    @Test
    fun mapsItemToProductRow() {
        val product = ItensViewModel.productFrom(
            ItemResponseDTO(
                id = "9c858901-8a57-4791-81fe-4c455b099bc9",
                name = "Notebook Dell Latitude",
                status = "PENDING_APPROVAL",
            ),
        )

        assertEquals("9c858901-8a57-4791-81fe-4c455b099bc9", product.id)
        assertEquals("Notebook Dell Latitude", product.name)
        assertEquals(ItemStatus.PendingApproval.label, product.statusText)
        assertEquals(ItemStatus.PendingApproval.colorFamily, product.statusStyle)
    }

    @Test
    fun omitsStatusTagWhenBackendHasNoChip() {
        val product = ItensViewModel.productFrom(
            ItemResponseDTO(id = "id-1", name = "Rascunho", status = "DRAFT"),
        )

        assertNull(product.statusText)
    }

    @Test
    fun formatsTotalItemsLabel() {
        assertEquals("1.230 Itens", ItensViewModel.totalItemsLabel(1230))
        assertEquals("1 Item", ItensViewModel.totalItemsLabel(1))
        assertEquals("0 Itens", ItensViewModel.totalItemsLabel(0))
    }

    @Test
    fun todosOmitsStatusAndCategory() {
        val query = ItensViewModel.queryFor(
            selectedFilter = ItensState.FILTER_ALL,
            searchQuery = "",
            categories = emptyList(),
        )

        assertNull(query.status)
        assertEquals(emptyList<String>(), query.extraStatuses)
        assertNull(query.categoryId)
        assertNull(query.q)
    }

    @Test
    fun pendentesRequestsBothStatuses() {
        val query = ItensViewModel.queryFor(
            selectedFilter = ItensState.FILTER_PENDING,
            searchQuery = "notebook",
            categories = emptyList(),
        )

        assertEquals("PENDING_APPROVAL", query.status)
        assertEquals(listOf("AWAITING_EVALUATION"), query.extraStatuses)
        assertNull(query.categoryId)
        assertEquals("notebook", query.q)
    }

    @Test
    fun categoryChipSendsCategoryId() {
        val query = ItensViewModel.queryFor(
            selectedFilter = "Informática",
            searchQuery = "  ",
            categories = listOf(CategoryResponseDTO(id = "c1", name = "Informática")),
        )

        assertNull(query.status)
        assertEquals("c1", query.categoryId)
        assertNull(query.q)
    }

    @Test
    fun buildsFilterChipsWithCategoryNames() {
        val options = ItensViewModel.filterOptionsFor(
            listOf(CategoryResponseDTO(id = "c1", name = "Informática")),
        )

        assertEquals(listOf("Todos", "Pendentes", "Informática"), options)
    }

    @Test
    fun keepsCategoriaPlaceholderWhenCategoriesAreEmpty() {
        assertEquals(
            listOf("Todos", "Pendentes", "Categoria"),
            ItensViewModel.filterOptionsFor(emptyList()),
        )
    }

    @Test
    fun mergesPendingPages() {
        val pending = PagedItemsDTO(
            content = listOf(ItemResponseDTO(id = "a", name = "A", status = "PENDING_APPROVAL")),
            totalElements = 1,
        )
        val awaiting = PagedItemsDTO(
            content = listOf(ItemResponseDTO(id = "b", name = "B", status = "AWAITING_EVALUATION")),
            totalElements = 2,
        )

        val merged = ItensViewModel.mergePages(pending, awaiting)

        assertEquals(listOf("a", "b"), merged.content.map { it.id })
        assertEquals(3L, merged.totalElements)
    }
}
