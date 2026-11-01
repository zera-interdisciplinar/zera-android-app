package com.zera.android.model.usecase.helpCentral

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GetHelpDocumentsTest {

    @Test
    fun readsOnlyCategoryKeys() {
        val flags = JsonObject(
            mapOf(
                "category_vidro" to JsonPrimitive("Vidro\nSepare"),
                "label_voltar" to JsonPrimitive("Voltar"),
                "new_checkout_flow" to JsonPrimitive(true),
            ),
        )

        val documents = GetHelpDocuments.documentsFrom(flags)

        assertEquals(listOf("category_vidro"), documents.map { it.key })
    }

    @Test
    fun ignoresNonStringValues() {
        val flags = JsonObject(
            mapOf(
                "category_bool" to JsonPrimitive(true),
                "category_number" to JsonPrimitive(3),
                "category_null" to JsonNull,
                "category_object" to JsonObject(mapOf("title" to JsonPrimitive("Vidro"))),
                "category_array" to JsonArray(listOf(JsonPrimitive("Vidro"))),
                "category_vidro" to JsonPrimitive("Vidro"),
            ),
        )

        val documents = GetHelpDocuments.documentsFrom(flags)

        assertEquals(listOf("category_vidro"), documents.map { it.key })
    }

    @Test
    fun ignoresEntriesWithoutTitle() {
        val flags = JsonObject(
            mapOf(
                "category_vazio" to JsonPrimitive("  "),
                "category_vidro" to JsonPrimitive("Vidro"),
            ),
        )

        val documents = GetHelpDocuments.documentsFrom(flags)

        assertEquals(listOf("category_vidro"), documents.map { it.key })
    }

    @Test
    fun sortsByTitleIgnoringAccentsAndCase() {
        val flags = JsonObject(
            mapOf(
                "category_vidro" to JsonPrimitive("Vidro"),
                "category_oleo_de_cozinha" to JsonPrimitive("Óleo de cozinha"),
                "category_metal" to JsonPrimitive("metal"),
                "category_eletronicos" to JsonPrimitive("Eletrônicos"),
            ),
        )

        val documents = GetHelpDocuments.documentsFrom(flags)

        assertEquals(
            listOf("Eletrônicos", "metal", "Óleo de cozinha", "Vidro"),
            documents.map { it.title },
        )
    }

    @Test
    fun emptyFlagsReturnEmptyList() {
        assertEquals(emptyList<Any>(), GetHelpDocuments.documentsFrom(JsonObject(emptyMap())))
    }

    @Test
    fun findReturnsDocumentByKey() {
        val useCase = GetHelpDocuments {
            JsonObject(
                mapOf(
                    "category_vidro" to JsonPrimitive("Vidro\nSepare\nCorpo"),
                    "category_metal" to JsonPrimitive("Metal"),
                ),
            )
        }

        val document = useCase.find("category_vidro")

        assertEquals("Vidro", document?.title)
        assertEquals("Corpo", document?.body)
    }

    @Test
    fun findReturnsNullForUnknownKey() {
        val useCase = GetHelpDocuments { JsonObject(mapOf("category_vidro" to JsonPrimitive("Vidro"))) }

        assertNull(useCase.find("category_metal"))
    }
}
