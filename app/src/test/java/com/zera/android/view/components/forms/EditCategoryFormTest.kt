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
class EditCategoryFormTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val nameChanges = mutableListOf<String>()
    private val descriptionChanges = mutableListOf<String>()
    private var saves = 0
    private var cancels = 0

    private fun showForm(
        nameError: String? = null,
        descriptionError: String? = null,
        isSubmitting: Boolean = false,
    ) {
        composeRule.setZeraContent {
            EditCategoryForm(
                name = "Informática",
                onNameChange = { nameChanges += it },
                description = "Notebooks",
                onDescriptionChange = { descriptionChanges += it },
                onSave = { saves++ },
                onCancel = { cancels++ },
                nameError = nameError,
                descriptionError = descriptionError,
                isSubmitting = isSubmitting,
            )
        }
    }

    @Test
    fun showsTheValues() {
        showForm()

        composeRule.onNodeWithText("Informática").assertIsDisplayed()
        composeRule.onNodeWithText("Notebooks").assertIsDisplayed()
    }

    @Test
    fun editingTheNameCallsOnlyItsCallback() {
        showForm()

        composeRule.onNodeWithText("Informática").performTextReplacement("Móveis")

        assertEquals(listOf("Móveis"), nameChanges)
        assertEquals(emptyList<String>(), descriptionChanges)
    }

    @Test
    fun editingTheDescriptionCallsOnlyItsCallback() {
        showForm()

        composeRule.onNodeWithText("Notebooks").performTextReplacement("Cadeiras")

        assertEquals(listOf("Cadeiras"), descriptionChanges)
        assertEquals(emptyList<String>(), nameChanges)
    }

    @Test
    fun showsTheFieldErrors() {
        showForm(nameError = "Nome inválido", descriptionError = "Descrição inválida")

        composeRule.onNodeWithText("Nome inválido").assertIsDisplayed()
        composeRule.onNodeWithText("Descrição inválida").assertIsDisplayed()
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

        composeRule.onNodeWithText("Informática").assertIsNotEnabled()
        composeRule.onNodeWithText("Notebooks").assertIsNotEnabled()
        composeRule.onNodeWithText("Salvar").assertDoesNotExist()
        composeRule.onNodeWithText("Salvando...").assertIsNotEnabled().performClick()
        composeRule.onNodeWithText("Cancelar").assertIsNotEnabled().performClick()
        assertEquals(0, saves)
        assertEquals(0, cancels)
    }
}
