package com.zera.android.view.components.forms

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
class SelectMaterialsFormTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val options = listOf("Plástico", "Metal")

    @Test
    fun showsTheTitleAndOptions() {
        composeRule.setZeraContent {
            SelectMaterialsForm(options = options, selected = emptyList(), onSelectedChange = {}, onConfirm = {})
        }

        composeRule.onNodeWithText("Selecione os materiais").assertIsDisplayed()
        composeRule.onNodeWithText("Plástico").assertIsDisplayed()
        composeRule.onNodeWithText("Metal").assertIsDisplayed()
    }

    @Test
    fun clickAddsTheMaterial() {
        val changes = mutableListOf<List<String>>()
        composeRule.setZeraContent {
            SelectMaterialsForm(
                options = options,
                selected = listOf("Plástico"),
                onSelectedChange = { changes += it },
                onConfirm = {},
            )
        }

        composeRule.onNodeWithText("Metal").performClick()

        assertEquals(listOf(listOf("Plástico", "Metal")), changes)
    }

    @Test
    fun clickRemovesTheMaterial() {
        val changes = mutableListOf<List<String>>()
        composeRule.setZeraContent {
            SelectMaterialsForm(
                options = options,
                selected = listOf("Plástico"),
                onSelectedChange = { changes += it },
                onConfirm = {},
            )
        }

        composeRule.onNodeWithText("Plástico").performClick()

        assertEquals(listOf(emptyList<String>()), changes)
    }

    @Test
    fun confirmCallsOnConfirm() {
        var confirmations = 0
        composeRule.setZeraContent {
            SelectMaterialsForm(
                options = options,
                selected = emptyList(),
                onSelectedChange = {},
                onConfirm = { confirmations++ },
            )
        }

        composeRule.onNodeWithText("Confirmar").performClick()

        assertEquals(1, confirmations)
    }
}
