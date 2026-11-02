package com.zera.android.view.components.inputs

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import com.zera.android.view.components.setZeraContent
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ZeraSearchInputTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val field = hasSetTextAction()

    @Test
    fun typingCallsOnValueChange() {
        val changes = mutableListOf<String>()
        composeRule.setZeraContent {
            ZeraSearchInput(value = "", onValueChange = { changes += it }, onSearch = {})
        }

        composeRule.onNode(field).performTextInput("notebook")

        assertEquals(listOf("notebook"), changes)
    }

    @Test
    fun showsThePlaceholderWhileEmpty() {
        composeRule.setZeraContent {
            ZeraSearchInput(value = "", onValueChange = {}, onSearch = {})
        }

        composeRule.onNodeWithText("Pesquisar").assertIsDisplayed()
    }

    @Test
    fun hidesThePlaceholderWithAValue() {
        composeRule.setZeraContent {
            ZeraSearchInput(value = "notebook", onValueChange = {}, onSearch = {})
        }

        composeRule.onNodeWithText("Pesquisar").assertDoesNotExist()
    }

    @Test
    fun emptySearchHasNoClearButton() {
        composeRule.setZeraContent {
            ZeraSearchInput(value = "", onValueChange = {}, onSearch = {})
        }

        composeRule.onNodeWithContentDescription("Limpar busca").assertDoesNotExist()
    }

    @Test
    fun clearButtonSendsAnEmptyValue() {
        val changes = mutableListOf<String>()
        composeRule.setZeraContent {
            ZeraSearchInput(value = "notebook", onValueChange = { changes += it }, onSearch = {})
        }

        composeRule.onNodeWithContentDescription("Limpar busca").performClick()

        assertEquals(listOf(""), changes)
    }

    @Test
    fun keyboardActionCallsOnSearch() {
        var searches = 0
        composeRule.setZeraContent {
            ZeraSearchInput(value = "notebook", onValueChange = {}, onSearch = { searches++ })
        }

        composeRule.onNode(field).performImeAction()

        assertEquals(1, searches)
    }
}
