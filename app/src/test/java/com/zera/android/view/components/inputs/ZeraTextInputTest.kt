package com.zera.android.view.components.inputs

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.text.AnnotatedString
import com.zera.android.view.components.setZeraContent
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ZeraTextInputTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val field = hasSetTextAction()

    // EditableText guarda o texto já transformado: mascarado, ele deixa de ser igual ao valor.
    private fun showsPlainText(value: String) =
        SemanticsMatcher.expectValue(SemanticsProperties.EditableText, AnnotatedString(value))

    @Test
    fun typingCallsOnValueChange() {
        val changes = mutableListOf<String>()
        composeRule.setZeraContent {
            ZeraTextInput(onValueChange = { changes += it })
        }

        composeRule.onNode(field).performTextInput("Maria")

        assertEquals(listOf("Maria"), changes)
    }

    @Test
    fun showsTheLabel() {
        composeRule.setZeraContent {
            ZeraTextInput(onValueChange = {}, value = "Maria", label = "Nome")
        }

        composeRule.onNodeWithText("NOME").assertIsDisplayed()
    }

    @Test
    fun omitsAnEmptyLabel() {
        composeRule.setZeraContent {
            ZeraTextInput(onValueChange = {}, value = "Maria", label = "")
        }

        composeRule.onNode(hasText("")).assertDoesNotExist()
    }

    @Test
    fun showsThePlaceholderWhileEmpty() {
        composeRule.setZeraContent {
            ZeraTextInput(onValueChange = {}, placeholder = "Seu nome")
        }

        composeRule.onNodeWithText("Seu nome").assertIsDisplayed()
    }

    @Test
    fun hidesThePlaceholderWithAValue() {
        composeRule.setZeraContent {
            ZeraTextInput(onValueChange = {}, value = "Maria", placeholder = "Seu nome")
        }

        composeRule.onNodeWithText("Seu nome").assertDoesNotExist()
    }

    @Test
    fun showsTheErrorMessageWhenInError() {
        composeRule.setZeraContent {
            ZeraTextInput(onValueChange = {}, isError = true, errorMessage = "E-mail inválido")
        }

        composeRule.onNodeWithText("E-mail inválido").assertIsDisplayed()
    }

    @Test
    fun hidesTheErrorMessageWithoutError() {
        composeRule.setZeraContent {
            ZeraTextInput(onValueChange = {}, isError = false, errorMessage = "E-mail inválido")
        }

        composeRule.onNodeWithText("E-mail inválido").assertDoesNotExist()
    }

    @Test
    fun disabledDoesNotAcceptText() {
        composeRule.setZeraContent {
            ZeraTextInput(onValueChange = {}, value = "Maria", enabled = false)
        }

        composeRule.onNodeWithText("Maria").assertIsNotEnabled()
        composeRule.onNode(field).assertDoesNotExist()
    }

    @Test
    fun passwordStartsMasked() {
        composeRule.setZeraContent {
            ZeraTextInput(onValueChange = {}, value = "segredo123", type = ZeraInputType.Password)
        }

        composeRule.onNode(field).assert(!showsPlainText("segredo123"))
        composeRule.onNodeWithText("Mostrar").assertIsDisplayed()
    }

    @Test
    fun toggleShowsAndHidesThePassword() {
        composeRule.setZeraContent {
            ZeraTextInput(onValueChange = {}, value = "segredo123", type = ZeraInputType.Password)
        }

        composeRule.onNodeWithText("Mostrar").performClick()
        composeRule.onNode(field).assert(showsPlainText("segredo123"))

        composeRule.onNodeWithText("Ocultar").performClick()
        composeRule.onNode(field).assert(!showsPlainText("segredo123"))
        composeRule.onNodeWithText("Mostrar").assertIsDisplayed()
    }

    @Test
    fun regularFieldHasNoToggle() {
        composeRule.setZeraContent {
            ZeraTextInput(onValueChange = {}, value = "Maria")
        }

        composeRule.onNode(field).assert(showsPlainText("Maria"))
        composeRule.onNodeWithText("Mostrar").assertDoesNotExist()
    }
}
