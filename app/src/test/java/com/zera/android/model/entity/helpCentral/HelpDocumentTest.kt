package com.zera.android.model.entity.helpCentral

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HelpDocumentTest {

    @Test
    fun splitsTitleSubtitleAndBody() {
        val value = "Vidro\nSepare, embale e identifique\n## Como descartar\n- Lave o recipiente"

        val document = HelpDocument.fromEntry("category_vidro", value)

        assertEquals(
            HelpDocument(
                key = "category_vidro",
                title = "Vidro",
                subtitle = "Separe, embale e identifique",
                body = "## Como descartar\n- Lave o recipiente",
            ),
            document,
        )
    }

    @Test
    fun keepsBlankLinesInsideBody() {
        val value = "Vidro\nSubtítulo\nParágrafo 1\n\nParágrafo 2"

        val document = HelpDocument.fromEntry("category_vidro", value)

        assertEquals("Parágrafo 1\n\nParágrafo 2", document?.body)
    }

    @Test
    fun skipsBlankLinesBeforeTitleAndSubtitle() {
        val value = "\n\nVidro\n\nSubtítulo\n\nCorpo"

        val document = HelpDocument.fromEntry("category_vidro", value)

        assertEquals("Vidro", document?.title)
        assertEquals("Subtítulo", document?.subtitle)
        assertEquals("Corpo", document?.body)
    }

    @Test
    fun removesHeadingMarkersFromTitleAndSubtitle() {
        val value = "# Vidro\n### Subtítulo\nCorpo"

        val document = HelpDocument.fromEntry("category_vidro", value)

        assertEquals("Vidro", document?.title)
        assertEquals("Subtítulo", document?.subtitle)
    }

    @Test
    fun normalizesWindowsLineBreaks() {
        val value = "Vidro\r\nSubtítulo\r\nLinha 1\r\nLinha 2"

        val document = HelpDocument.fromEntry("category_vidro", value)

        assertEquals("Subtítulo", document?.subtitle)
        assertEquals("Linha 1\nLinha 2", document?.body)
    }

    @Test
    fun titleOnlyHasEmptySubtitleAndBody() {
        val document = HelpDocument.fromEntry("category_vidro", "  Vidro  ")

        assertEquals("Vidro", document?.title)
        assertEquals("", document?.subtitle)
        assertEquals("", document?.body)
    }

    @Test
    fun blankValueReturnsNull() {
        assertNull(HelpDocument.fromEntry("category_vidro", ""))
        assertNull(HelpDocument.fromEntry("category_vidro", " \n \n"))
        assertNull(HelpDocument.fromEntry("category_vidro", "#\nSubtítulo"))
    }
}
