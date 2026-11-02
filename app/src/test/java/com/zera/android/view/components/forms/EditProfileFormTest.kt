package com.zera.android.view.components.forms

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
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
class EditProfileFormTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val nameChanges = mutableListOf<String>()
    private val emailChanges = mutableListOf<String>()
    private val phoneChanges = mutableListOf<String>()
    private var saves = 0
    private var cancels = 0

    private val name = "Natália"
    private val email = "natalia@empresa.com"
    private val phone = "11987654321"

    private fun showForm(
        nameEnabled: Boolean = true,
        emailEnabled: Boolean = true,
        phoneEnabled: Boolean = true,
        nameError: String? = null,
        emailError: String? = null,
        phoneError: String? = null,
        errorMessage: String? = null,
        isSubmitting: Boolean = false,
    ) {
        composeRule.setZeraContent {
            EditProfileForm(
                name = name,
                onNameChange = { nameChanges += it },
                email = email,
                onEmailChange = { emailChanges += it },
                phone = phone,
                onPhoneChange = { phoneChanges += it },
                onSave = { saves++ },
                onCancel = { cancels++ },
                nameEnabled = nameEnabled,
                emailEnabled = emailEnabled,
                phoneEnabled = phoneEnabled,
                nameError = nameError,
                emailError = emailError,
                phoneError = phoneError,
                errorMessage = errorMessage,
                isSubmitting = isSubmitting,
            )
        }
    }

    @Test
    fun showsTheValues() {
        showForm()

        composeRule.onNodeWithText(name).assertIsDisplayed()
        composeRule.onNodeWithText(email).assertIsDisplayed()
        composeRule.onNodeWithText(phone).assertIsDisplayed()
    }

    @Test
    fun editingCallsOnlyTheCallbackOfTheField() {
        showForm()

        composeRule.onNodeWithText(email).performTextReplacement("nova@empresa.com")

        assertEquals(listOf("nova@empresa.com"), emailChanges)
        assertEquals(emptyList<String>(), nameChanges)
        assertEquals(emptyList<String>(), phoneChanges)
    }

    @Test
    fun blockedNameKeepsTheOtherFieldsEnabled() {
        showForm(nameEnabled = false)

        composeRule.onNodeWithText(name).assertIsDisplayed().assertIsNotEnabled()
        composeRule.onNodeWithText(email).assertIsEnabled()
        composeRule.onNodeWithText(phone).assertIsEnabled()
    }

    @Test
    fun blockedEmailKeepsTheOtherFieldsEnabled() {
        showForm(emailEnabled = false)

        composeRule.onNodeWithText(email).assertIsDisplayed().assertIsNotEnabled()
        composeRule.onNodeWithText(name).assertIsEnabled()
        composeRule.onNodeWithText(phone).assertIsEnabled()
    }

    @Test
    fun blockedPhoneKeepsTheOtherFieldsEnabled() {
        showForm(phoneEnabled = false)

        composeRule.onNodeWithText(phone).assertIsDisplayed().assertIsNotEnabled()
        composeRule.onNodeWithText(name).assertIsEnabled()
        composeRule.onNodeWithText(email).assertIsEnabled()
    }

    @Test
    fun showsTheFieldErrorsAndTheGeneralError() {
        showForm(
            nameError = "Nome inválido",
            emailError = "E-mail inválido",
            phoneError = "Telefone inválido",
            errorMessage = "Não foi possível salvar",
        )

        composeRule.onNodeWithText("Nome inválido").assertIsDisplayed()
        composeRule.onNodeWithText("E-mail inválido").assertIsDisplayed()
        composeRule.onNodeWithText("Telefone inválido").assertIsDisplayed()
        composeRule.onNodeWithText("Não foi possível salvar").assertIsDisplayed()
    }

    @Test
    fun saveAndCancelCallTheirCallbacks() {
        showForm()

        composeRule.onNodeWithText("Salvar").performClick()
        assertEquals(1, saves)
        assertEquals(0, cancels)

        composeRule.onNodeWithText("Cancelar").performClick()
        assertEquals(1, saves)
        assertEquals(1, cancels)
    }

    @Test
    fun submittingBlocksTheForm() {
        showForm(isSubmitting = true)

        composeRule.onNodeWithText(name).assertIsNotEnabled()
        composeRule.onNodeWithText(email).assertIsNotEnabled()
        composeRule.onNodeWithText(phone).assertIsNotEnabled()
        composeRule.onNodeWithText("Salvar").assertDoesNotExist()
        composeRule.onNodeWithText("Salvando...").assertIsNotEnabled().performClick()
        composeRule.onNodeWithText("Cancelar").assertIsNotEnabled().performClick()
        assertEquals(0, saves)
        assertEquals(0, cancels)
    }
}
