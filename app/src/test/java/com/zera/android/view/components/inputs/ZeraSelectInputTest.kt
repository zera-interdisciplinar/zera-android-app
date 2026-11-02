package com.zera.android.view.components.inputs

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
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
class ZeraSelectInputTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun showsTheValue() {
        composeRule.setZeraContent {
            ZeraSelectInput(onClick = {}, value = "Plástico, Bateria", placeholder = "Selecione")
        }

        composeRule.onNodeWithText("Plástico, Bateria").assertIsDisplayed()
        composeRule.onNodeWithText("Selecione").assertDoesNotExist()
    }

    @Test
    fun showsThePlaceholderWhileEmpty() {
        composeRule.setZeraContent {
            ZeraSelectInput(onClick = {}, placeholder = "Selecione")
        }

        composeRule.onNodeWithText("Selecione").assertIsDisplayed()
    }

    @Test
    fun clickCallsOnClick() {
        var clicks = 0
        composeRule.setZeraContent {
            ZeraSelectInput(onClick = { clicks++ }, placeholder = "Selecione")
        }

        composeRule.onNodeWithText("Selecione").performClick()

        assertEquals(1, clicks)
    }

    @Test
    fun disabledIgnoresClicks() {
        var clicks = 0
        composeRule.setZeraContent {
            ZeraSelectInput(onClick = { clicks++ }, placeholder = "Selecione", enabled = false)
        }

        composeRule.onNodeWithText("Selecione").assertIsNotEnabled().performClick()

        assertEquals(0, clicks)
    }

    @Test
    fun showsTheErrorMessageWhenInError() {
        composeRule.setZeraContent {
            ZeraSelectInput(onClick = {}, isError = true, errorMessage = "Selecione ao menos um")
        }

        composeRule.onNodeWithText("Selecione ao menos um").assertIsDisplayed()
    }

    @Test
    fun hidesTheErrorMessageWithoutError() {
        composeRule.setZeraContent {
            ZeraSelectInput(onClick = {}, isError = false, errorMessage = "Selecione ao menos um")
        }

        composeRule.onNodeWithText("Selecione ao menos um").assertDoesNotExist()
    }
}
