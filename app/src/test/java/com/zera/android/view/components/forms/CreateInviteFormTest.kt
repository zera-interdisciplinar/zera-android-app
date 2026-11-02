package com.zera.android.view.components.forms

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
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
class CreateInviteFormTest {
    @get:Rule
    val composeRule = createComposeRule()

    // O título e o botão têm o mesmo texto.
    private val submitButton = hasClickAction() and hasText("Criar convite")

    @Test
    fun showsTheName() {
        composeRule.setZeraContent {
            CreateInviteForm(name = "Carol", onNameChange = {}, onSubmit = {})
        }

        composeRule.onNodeWithText("Carol").assertIsDisplayed()
        composeRule.onNodeWithText("NOME DO CONVITE").assertIsDisplayed()
    }

    @Test
    fun editingCallsOnNameChange() {
        val changes = mutableListOf<String>()
        composeRule.setZeraContent {
            CreateInviteForm(name = "Carol", onNameChange = { changes += it }, onSubmit = {})
        }

        composeRule.onNodeWithText("Carol").performTextReplacement("Ana")

        assertEquals(listOf("Ana"), changes)
    }

    @Test
    fun submitCallsOnSubmit() {
        var submits = 0
        composeRule.setZeraContent {
            CreateInviteForm(name = "Carol", onNameChange = {}, onSubmit = { submits++ })
        }

        composeRule.onNode(submitButton).performClick()

        assertEquals(1, submits)
    }

    @Test
    fun showsTheNameError() {
        composeRule.setZeraContent {
            CreateInviteForm(name = "", onNameChange = {}, onSubmit = {}, nameError = "Informe o nome")
        }

        composeRule.onNodeWithText("Informe o nome").assertIsDisplayed()
    }

    @Test
    fun submittingBlocksTheForm() {
        var submits = 0
        composeRule.setZeraContent {
            CreateInviteForm(name = "Carol", onNameChange = {}, onSubmit = { submits++ }, isSubmitting = true)
        }

        composeRule.onNodeWithText("Carol").assertIsNotEnabled()
        composeRule.onNode(submitButton).assertDoesNotExist()
        composeRule.onNodeWithText("Enviando...").assertIsNotEnabled().performClick()
        assertEquals(0, submits)
    }
}
