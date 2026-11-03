package com.zera.android.view.components.forms

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import com.zera.android.view.components.setZeraContent
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EditModelFormTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val nameChanges = mutableListOf<String>()
    private val manufacturerChanges = mutableListOf<String>()
    private val weightChanges = mutableListOf<String>()
    private var saves = 0
    private var cancels = 0

    private fun showForm(
        nameError: String? = null,
        manufacturerError: String? = null,
        weightError: String? = null,
        isSubmitting: Boolean = false,
    ) {
        composeRule.setZeraContent {
            EditModelForm(
                name = "Latitude 5420",
                onNameChange = { nameChanges += it },
                manufacturer = "Dell",
                onManufacturerChange = { manufacturerChanges += it },
                weight = "2,5",
                onWeightChange = { weightChanges += it },
                onSave = { saves++ },
                onCancel = { cancels++ },
                nameError = nameError,
                manufacturerError = manufacturerError,
                weightError = weightError,
                isSubmitting = isSubmitting,
            )
        }
    }

    @Test
    fun showsTheValues() {
        showForm()

        composeRule.onNodeWithText("Latitude 5420").assertIsDisplayed()
        composeRule.onNodeWithText("Dell").assertIsDisplayed()
    }

    @Test
    fun editingTheNameCallsOnlyItsCallback() {
        showForm()

        composeRule.onNodeWithText("Latitude 5420").performTextReplacement("XPS 13")

        assertEquals(listOf("XPS 13"), nameChanges)
        assertEquals(emptyList<String>(), manufacturerChanges)
    }

    @Test
    fun editingTheWeightCallsOnlyItsCallback() {
        showForm()

        composeRule.onNodeWithText("2,5").performTextReplacement("3")

        assertEquals(listOf("3"), weightChanges)
        assertEquals(emptyList<String>(), nameChanges)
        assertEquals(emptyList<String>(), manufacturerChanges)
    }

    @Test
    fun editingTheManufacturerCallsOnlyItsCallback() {
        showForm()

        composeRule.onNodeWithText("Dell").performTextReplacement("Lenovo")

        assertEquals(listOf("Lenovo"), manufacturerChanges)
        assertEquals(emptyList<String>(), nameChanges)
    }

    @Test
    fun showsTheFieldErrors() {
        showForm(nameError = "Nome inválido", manufacturerError = "Marca inválida")

        composeRule.onNodeWithText("Nome inválido").assertIsDisplayed()
        composeRule.onNodeWithText("Marca inválida").assertIsDisplayed()
    }

    @Test
    fun saveCallsOnSave() {
        showForm()

        composeRule.onNodeWithText("Salvar").performClick()

        assertEquals(1, saves)
        assertEquals(0, cancels)
    }

    @Test
    fun cancelCallsOnCancel() {
        showForm()

        composeRule.onNodeWithText("Cancelar").performClick()

        assertEquals(1, cancels)
        assertEquals(0, saves)
    }

    @Test
    fun submittingBlocksTheForm() {
        showForm(isSubmitting = true)

        composeRule.onNodeWithText("Latitude 5420").assertIsNotEnabled()
        composeRule.onNodeWithText("Dell").assertIsNotEnabled()
        composeRule.onNodeWithText("Salvar").assertDoesNotExist()
        composeRule.onNodeWithText("Salvando...").assertIsNotEnabled().performClick()
        composeRule.onNodeWithText("Cancelar").assertIsNotEnabled().performClick()
        assertEquals(0, saves)
        assertEquals(0, cancels)
    }
}
