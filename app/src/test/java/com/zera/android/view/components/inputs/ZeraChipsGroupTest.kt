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
class ZeraChipsGroupTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val options = listOf("Plástico", "Metal")

    @Test
    fun showsTheLabelAndOptions() {
        composeRule.setZeraContent {
            ZeraChipsGroup(options = options, label = "Materiais")
        }

        composeRule.onNodeWithText("Materiais").assertIsDisplayed()
        composeRule.onNodeWithText("Plástico").assertIsDisplayed()
        composeRule.onNodeWithText("Metal").assertIsDisplayed()
    }

    @Test
    fun singleSelectionCallsOnSelectedChange() {
        val changes = mutableListOf<String>()
        composeRule.setZeraContent {
            ZeraChipsGroup(options = options, selected = "Plástico", onSelectedChange = { changes += it })
        }

        composeRule.onNodeWithText("Metal").performClick()

        assertEquals(listOf("Metal"), changes)
    }

    @Test
    fun multipleSelectionAddsTheOption() {
        val changes = mutableListOf<List<String>>()
        composeRule.setZeraContent {
            ZeraChipsGroup(
                options = options,
                selectMany = true,
                selectedValues = listOf("Plástico"),
                onSelectedValuesChange = { changes += it },
            )
        }

        composeRule.onNodeWithText("Metal").performClick()

        assertEquals(listOf(listOf("Plástico", "Metal")), changes)
    }

    @Test
    fun multipleSelectionRemovesTheOption() {
        val changes = mutableListOf<List<String>>()
        composeRule.setZeraContent {
            ZeraChipsGroup(
                options = options,
                selectMany = true,
                selectedValues = listOf("Plástico", "Metal"),
                onSelectedValuesChange = { changes += it },
            )
        }

        composeRule.onNodeWithText("Plástico").performClick()

        assertEquals(listOf(listOf("Metal")), changes)
    }

    @Test
    fun multipleSelectionDoesNotCallOnSelectedChange() {
        val changes = mutableListOf<String>()
        composeRule.setZeraContent {
            ZeraChipsGroup(options = options, selectMany = true, onSelectedChange = { changes += it })
        }

        composeRule.onNodeWithText("Metal").performClick()

        assertEquals(emptyList<String>(), changes)
    }

    @Test
    fun stackedLayoutBehavesTheSame() {
        val changes = mutableListOf<List<String>>()
        composeRule.setZeraContent {
            ZeraChipsGroup(
                options = options,
                stacked = true,
                selectMany = true,
                selectedValues = listOf("Plástico"),
                onSelectedValuesChange = { changes += it },
            )
        }

        composeRule.onNodeWithText("Metal").performClick()
        composeRule.onNodeWithText("Plástico").performClick()

        assertEquals(listOf(listOf("Plástico", "Metal"), emptyList()), changes)
    }

    @Test
    fun stackedSingleSelectionCallsOnSelectedChange() {
        val changes = mutableListOf<String>()
        composeRule.setZeraContent {
            ZeraChipsGroup(options = options, stacked = true, onSelectedChange = { changes += it })
        }

        composeRule.onNodeWithText("Metal").performClick()

        assertEquals(listOf("Metal"), changes)
    }

    @Test
    fun disabledIgnoresClicks() {
        val changes = mutableListOf<String>()
        composeRule.setZeraContent {
            ZeraChipsGroup(options = options, onSelectedChange = { changes += it }, enabled = false)
        }

        composeRule.onNodeWithText("Metal").assertIsNotEnabled().performClick()

        assertEquals(emptyList<String>(), changes)
    }
}
