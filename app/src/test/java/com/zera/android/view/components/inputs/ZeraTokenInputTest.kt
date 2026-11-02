package com.zera.android.view.components.inputs

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import com.zera.android.view.components.setZeraContent
import org.junit.Assert.assertEquals
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ZeraTokenInputTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val field = hasSetTextAction()

    @Test
    fun keepsOnlyDigitsUpToTheSize() {
        val changes = mutableListOf<String>()
        composeRule.setZeraContent {
            ZeraTokenInput(value = "", onValueChange = { changes += it })
        }

        composeRule.onNode(field).performTextInput("12a345678")

        assertEquals(listOf("123456"), changes)
    }

    @Test
    fun respectsACustomSize() {
        val changes = mutableListOf<String>()
        composeRule.setZeraContent {
            ZeraTokenInput(value = "", onValueChange = { changes += it }, size = 4)
        }

        composeRule.onNode(field).performTextInput("123456")

        assertEquals(listOf("1234"), changes)
    }

    @Test
    fun normalizesTheExternalValue() {
        composeRule.setZeraContent {
            ZeraTokenInput(value = "1a2", onValueChange = {})
        }

        composeRule.onNode(field).assert(hasText("12"))
    }

    @Test
    fun ignoresInputWithoutNewDigits() {
        val changes = mutableListOf<String>()
        composeRule.setZeraContent {
            ZeraTokenInput(value = "", onValueChange = { changes += it })
        }

        composeRule.onNode(field).performTextInput("abc")

        assertEquals(emptyList<String>(), changes)
    }

    @Test
    fun completingTheCodeCallsOnFilled() {
        val filled = mutableListOf<String>()
        composeRule.setZeraContent {
            ZeraTokenInput(value = "", onValueChange = {}, onFilled = { filled += it })
        }

        composeRule.onNode(field).performTextInput("123456")

        assertEquals(listOf("123456"), filled)
    }

    @Test
    fun partialCodeDoesNotCallOnFilled() {
        val filled = mutableListOf<String>()
        composeRule.setZeraContent {
            ZeraTokenInput(value = "", onValueChange = {}, onFilled = { filled += it })
        }

        composeRule.onNode(field).performTextInput("123")

        assertEquals(emptyList<String>(), filled)
    }

    @Test
    fun doneWithPartialCodeDoesNotCallOnFilled() {
        val filled = mutableListOf<String>()
        composeRule.setZeraContent {
            ZeraTokenInput(value = "123", onValueChange = {}, onFilled = { filled += it })
        }

        composeRule.onNode(field).performImeAction()

        assertEquals(emptyList<String>(), filled)
    }

    @Test
    fun doneWithFullCodeCallsOnFilled() {
        val filled = mutableListOf<String>()
        composeRule.setZeraContent {
            ZeraTokenInput(value = "123456", onValueChange = {}, onFilled = { filled += it })
        }

        composeRule.onNode(field).performImeAction()

        assertEquals(listOf("123456"), filled)
    }

    @Ignore("Problema conhecido 1 da spec testes-componentes-visuais: onFilled dispara duas vezes")
    @Test
    fun doneAfterCompletingTheCodeDoesNotCallOnFilledAgain() {
        val filled = mutableListOf<String>()
        composeRule.setZeraContent {
            var code by remember { mutableStateOf("") }
            ZeraTokenInput(value = code, onValueChange = { code = it }, onFilled = { filled += it })
        }

        composeRule.onNode(field).performTextInput("123456")
        composeRule.onNode(field).performImeAction()

        assertEquals(listOf("123456"), filled)
    }

    @Test
    fun disabledDoesNotAcceptText() {
        composeRule.setZeraContent {
            ZeraTokenInput(value = "123", onValueChange = {}, enabled = false)
        }

        composeRule.onNode(field).assertDoesNotExist()
    }

    @Test
    fun showsTheLabel() {
        composeRule.setZeraContent {
            ZeraTokenInput(value = "", onValueChange = {}, label = "Código de convite")
        }

        composeRule.onNodeWithText("CÓDIGO DE CONVITE").assertIsDisplayed()
    }

    @Test
    fun showsTheErrorMessageWhenInError() {
        composeRule.setZeraContent {
            ZeraTokenInput(value = "", onValueChange = {}, isError = true, errorMessage = "Código inválido")
        }

        composeRule.onNodeWithText("Código inválido").assertIsDisplayed()
    }

    @Test
    fun hidesTheErrorMessageWithoutError() {
        composeRule.setZeraContent {
            ZeraTokenInput(value = "", onValueChange = {}, isError = false, errorMessage = "Código inválido")
        }

        composeRule.onNodeWithText("Código inválido").assertDoesNotExist()
    }
}
