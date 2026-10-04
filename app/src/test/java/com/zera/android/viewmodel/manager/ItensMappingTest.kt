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
    fun noSelectedFiltersRequestsAllItems() {
        val queries = ItensViewModel.queriesFor(
            selectedStatuses = emptyList(),
            selectedCategories = emptyList(),
            searchQuery = "",
            categories = emptyList(),
        )

        assertEquals(listOf(ItemsQuery()), queries)
    }

    @Test
    fun statusAndCategorySelectionsCreateCombinedQueries() {
        val queries = ItensViewModel.queriesFor(
            selectedStatuses = listOf("Pendente", "Recusado"),
            selectedCategories = listOf("Informática", "Móveis"),
            searchQuery = "notebook",
            categories = listOf(
                CategoryResponseDTO(id = "c1", name = "Informática"),
                CategoryResponseDTO(id = "c2", name = "Móveis"),
            ),
        )

        assertEquals(
            listOf(
                ItemsQuery("PENDING_APPROVAL", "c1", "notebook"),
                ItemsQuery("PENDING_APPROVAL", "c2", "notebook"),
                ItemsQuery("REJECTED", "c1", "notebook"),
                ItemsQuery("REJECTED", "c2", "notebook"),
            ),
            queries,
        )
    }

    @Test
    fun categorySelectionSendsCategoryId() {
        val queries = ItensViewModel.queriesFor(
            selectedStatuses = emptyList(),
            selectedCategories = listOf("Informática"),
            searchQuery = "  ",
            categories = listOf(CategoryResponseDTO(id = "c1", name = "Informática")),
        )

        assertEquals(listOf(ItemsQuery(categoryId = "c1")), queries)
    }

    @Test
    fun mergesPendingPages() {
        val pending = PagedItemsDTO(
            content = listOf(ItemResponseDTO(id = "a", name = "A", status = "PENDING_APPROVAL")),
            page = 0,
            size = 20,
            totalElements = 1,
            totalPages = 1,
        )
        val awaiting = PagedItemsDTO(
            content = listOf(ItemResponseDTO(id = "b", name = "B", status = "AWAITING_EVALUATION")),
            page = 0,
            size = 20,
            totalElements = 2,
            totalPages = 3,
        )

        val merged = ItensViewModel.mergePages(listOf(pending, awaiting))

        assertEquals(listOf("a", "b"), merged.content.map { it.id })
        assertEquals(3L, merged.totalElements)
        assertEquals(3, merged.totalPages)
        assertEquals(0, merged.page)
    }

    @Test
    fun canLoadMoreWhenNextPageExists() {
        assertEquals(
            true,
            ItensViewModel.canLoadMore(loadedPage = 0, totalPages = 62, isBusy = false),
        )
    }

    @Test
    fun cannotLoadMoreOnLastPage() {
        assertEquals(
            false,
            ItensViewModel.canLoadMore(loadedPage = 61, totalPages = 62, isBusy = false),
        )
    }

    @Test
    fun cannotLoadMoreWhileBusy() {
        assertEquals(
            false,
            ItensViewModel.canLoadMore(loadedPage = 0, totalPages = 2, isBusy = true),
        )
    }

    @Test
    fun appendsNextPageWithoutDuplicates() {
        val current = listOf(
            ItensViewModel.productFrom(ItemResponseDTO(id = "a", name = "A", status = "IN_STOCK")),
        )
        val incoming = listOf(
            ItemResponseDTO(id = "a", name = "A", status = "IN_STOCK"),
            ItemResponseDTO(id = "b", name = "B", status = "IN_STOCK"),
        )

        val appended = ItensViewModel.appendProducts(current, incoming)

        assertEquals(listOf("a", "b"), appended.map { it.id })
    }
}
