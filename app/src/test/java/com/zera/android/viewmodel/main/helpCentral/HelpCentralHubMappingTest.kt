package com.zera.android.viewmodel.main.helpCentral

import com.zera.android.model.entity.helpCentral.HelpDocument
import org.junit.Assert.assertEquals
import org.junit.Test

class HelpCentralHubMappingTest {
    private val documents = listOf(
        HelpDocument(key = "category_vidro", title = "Vidro", subtitle = "Separe, embale e identifique", body = ""),
        HelpDocument(key = "category_lampadas", title = "Lâmpadas", subtitle = "Nunca quebre lâmpada fluorescente", body = ""),
        HelpDocument(key = "category_metal", title = "Metal", subtitle = "", body = ""),
    )

    @Test
    fun mapsDocumentToOption() {
        val option = HelpCentralHubViewModel.optionFrom(documents.first())

        assertEquals("category_vidro", option.id)
        assertEquals("Vidro", option.name)
        assertEquals("Separe, embale e identifique", option.description)
    }

    @Test
    fun searchMatchesTitleIgnoringCase() {
        assertEquals(
            listOf("category_vidro"),
            HelpCentralHubViewModel.visibleDocuments(documents, "VID").map { it.key },
        )
    }

    @Test
    fun searchMatchesSubtitle() {
        assertEquals(
            listOf("category_lampadas"),
            HelpCentralHubViewModel.visibleDocuments(documents, "fluorescente").map { it.key },
        )
    }

    @Test
    fun blankSearchReturnsAllDocuments() {
        assertEquals(documents, HelpCentralHubViewModel.visibleDocuments(documents, "  "))
    }

    @Test
    fun searchTrimsTerm() {
        assertEquals(
            listOf("category_metal"),
            HelpCentralHubViewModel.visibleDocuments(documents, " metal ").map { it.key },
        )
    }

    @Test
    fun searchWithoutMatchReturnsEmpty() {
        assertEquals(emptyList<HelpDocument>(), HelpCentralHubViewModel.visibleDocuments(documents, "papel"))
    }
}
