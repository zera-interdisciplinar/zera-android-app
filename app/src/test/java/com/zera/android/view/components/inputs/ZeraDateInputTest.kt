package com.zera.android.view.components.inputs

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.zera.android.view.components.setZeraContent
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.TimeZone

@RunWith(RobolectricTestRunner::class)
class ZeraDateInputTest {
    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var originalTimeZone: TimeZone

    // Atrás do UTC: é onde um erro de conversão faria a data voltar um dia.
    @Before
    fun useTimeZoneBehindUtc() {
        originalTimeZone = TimeZone.getDefault()
        TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"))
    }

    @After
    fun restoreTimeZone() {
        TimeZone.setDefault(originalTimeZone)
    }

    @Test
    fun showsTheValue() {
        composeRule.setZeraContent {
            ZeraDateInput(onValueChange = {}, value = "15/03/2025", placeholder = "DD/MM/AAAA")
        }

        composeRule.onNodeWithText("15/03/2025").assertIsDisplayed()
        composeRule.onNodeWithText("DD/MM/AAAA").assertDoesNotExist()
    }

    @Test
    fun showsThePlaceholderWhileEmpty() {
        composeRule.setZeraContent {
            ZeraDateInput(onValueChange = {}, placeholder = "DD/MM/AAAA")
        }

        composeRule.onNodeWithText("DD/MM/AAAA").assertIsDisplayed()
    }

    @Test
    fun clickOpensThePicker() {
        composeRule.setZeraContent {
            ZeraDateInput(onValueChange = {}, placeholder = "DD/MM/AAAA")
        }

        composeRule.onNodeWithText("Confirmar").assertDoesNotExist()
        composeRule.onNodeWithText("DD/MM/AAAA").performClick()

        composeRule.onNodeWithText("Confirmar").assertIsDisplayed()
    }

    @Test
    fun confirmKeepsTheSameDate() {
        val changes = mutableListOf<String>()
        composeRule.setZeraContent {
            ZeraDateInput(onValueChange = { changes += it }, value = "15/03/2025")
        }

        composeRule.onNodeWithText("15/03/2025").performClick()
        composeRule.onNodeWithText("Confirmar").performClick()

        assertEquals(listOf("15/03/2025"), changes)
        composeRule.onNodeWithText("Confirmar").assertDoesNotExist()
    }

    @Test
    fun confirmKeepsTheFirstDayOfTheYear() {
        val changes = mutableListOf<String>()
        composeRule.setZeraContent {
            ZeraDateInput(onValueChange = { changes += it }, value = "01/01/2025")
        }

        composeRule.onNodeWithText("01/01/2025").performClick()
        composeRule.onNodeWithText("Confirmar").performClick()

        assertEquals(listOf("01/01/2025"), changes)
    }

    @Test
    fun cancelClosesWithoutChanging() {
        val changes = mutableListOf<String>()
        composeRule.setZeraContent {
            ZeraDateInput(onValueChange = { changes += it }, value = "15/03/2025")
        }

        composeRule.onNodeWithText("15/03/2025").performClick()
        composeRule.onNodeWithText("Cancelar").performClick()

        assertEquals(emptyList<String>(), changes)
        composeRule.onNodeWithText("Confirmar").assertDoesNotExist()
    }

    @Test
    fun confirmWithoutADateClosesWithoutChanging() {
        val changes = mutableListOf<String>()
        composeRule.setZeraContent {
            ZeraDateInput(onValueChange = { changes += it }, placeholder = "DD/MM/AAAA")
        }

        composeRule.onNodeWithText("DD/MM/AAAA").performClick()
        composeRule.onNodeWithText("Confirmar").performClick()

        assertEquals(emptyList<String>(), changes)
        composeRule.onNodeWithText("Confirmar").assertDoesNotExist()
    }

    @Test
    fun invalidValueOpensWithoutADate() {
        val changes = mutableListOf<String>()
        composeRule.setZeraContent {
            ZeraDateInput(onValueChange = { changes += it }, value = "data inválida")
        }

        composeRule.onNodeWithText("data inválida").performClick()
        composeRule.onNodeWithText("Confirmar").performClick()

        assertEquals(emptyList<String>(), changes)
    }

    @Test
    fun disabledDoesNotOpen() {
        composeRule.setZeraContent {
            ZeraDateInput(onValueChange = {}, placeholder = "DD/MM/AAAA", enabled = false)
        }

        composeRule.onNodeWithText("DD/MM/AAAA").assertIsNotEnabled().performClick()

        composeRule.onNodeWithText("Confirmar").assertDoesNotExist()
    }

    @Test
    fun showsTheErrorMessageWhenInError() {
        composeRule.setZeraContent {
            ZeraDateInput(onValueChange = {}, isError = true, errorMessage = "Data obrigatória")
        }

        composeRule.onNodeWithText("Data obrigatória").assertIsDisplayed()
    }

    @Test
    fun hidesTheErrorMessageWithoutError() {
        composeRule.setZeraContent {
            ZeraDateInput(onValueChange = {}, isError = false, errorMessage = "Data obrigatória")
        }

        composeRule.onNodeWithText("Data obrigatória").assertDoesNotExist()
    }
}
