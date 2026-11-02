package com.zera.android.view.components.inputs

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.zera.android.view.components.setZeraContent
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ZeraDropdownInputTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val options = listOf("Novo", "Usado")

    @Test
    fun showsTheValue() {
        composeRule.setZeraContent {
            ZeraDropdownInput(values = options, onValueChange = {}, value = "Usado", placeholder = "Selecione")
        }

        composeRule.onNodeWithText("Usado").assertIsDisplayed()
        composeRule.onNodeWithText("Selecione").assertDoesNotExist()
    }

    @Test
    fun showsThePlaceholderWhileEmpty() {
        composeRule.setZeraContent {
            ZeraDropdownInput(values = options, onValueChange = {}, placeholder = "Selecione")
        }

        composeRule.onNodeWithText("Selecione").assertIsDisplayed()
    }

    @Test
    fun startsClosed() {
        composeRule.setZeraContent {
            ZeraDropdownInput(values = options, onValueChange = {}, placeholder = "Selecione")
        }

        composeRule.onNodeWithText("Novo").assertDoesNotExist()
    }

    @Test
    fun clickOpensTheOptions() {
        composeRule.setZeraContent {
            ZeraDropdownInput(values = options, onValueChange = {}, placeholder = "Selecione")
        }

        composeRule.onNodeWithText("Selecione").performClick()

        composeRule.onNodeWithText("Novo").assertIsDisplayed()
        composeRule.onNodeWithText("Usado").assertIsDisplayed()
    }

    @Test
    fun choosingAnOptionCallsOnValueChangeAndCloses() {
        val changes = mutableListOf<String>()
        composeRule.setZeraContent {
            ZeraDropdownInput(values = options, onValueChange = { changes += it }, placeholder = "Selecione")
        }

        composeRule.onNodeWithText("Selecione").performClick()
        composeRule.onNodeWithText("Usado").performClick()

        assertEquals(listOf("Usado"), changes)
        composeRule.onNodeWithText("Novo").assertDoesNotExist()
    }

    @Test
    fun disabledDoesNotOpen() {
        composeRule.setZeraContent {
            ZeraDropdownInput(values = options, onValueChange = {}, placeholder = "Selecione", enabled = false)
        }

        composeRule.onNodeWithText("Selecione").performClick()

        composeRule.onNodeWithText("Novo").assertDoesNotExist()
    }

    @Test
    fun showsTheErrorMessageWhenInError() {
        composeRule.setZeraContent {
            ZeraDropdownInput(values = options, onValueChange = {}, isError = true, errorMessage = "Obrigatório")
        }

        composeRule.onNodeWithText("Obrigatório").assertIsDisplayed()
    }

    @Test
    fun hidesTheErrorMessageWithoutError() {
        composeRule.setZeraContent {
            ZeraDropdownInput(values = options, onValueChange = {}, isError = false, errorMessage = "Obrigatório")
        }

        composeRule.onNodeWithText("Obrigatório").assertDoesNotExist()
    }
}
