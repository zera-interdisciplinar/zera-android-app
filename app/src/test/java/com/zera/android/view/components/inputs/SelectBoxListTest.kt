package com.zera.android.view.components.inputs

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.zera.android.view.components.setZeraContent
import com.zera.android.view.components.texts.BodyText
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SelectBoxListTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val options = listOf(
        SelectOption(name = "Notebook", value = "n"),
        SelectOption(name = "Teclado", value = "t"),
    )
    private val selectAll = "Selecionar Todos"

    @Test
    fun clickAddsTheOption() {
        val changes = mutableListOf<Set<String>>()
        composeRule.setZeraContent {
            SelectBoxList(options = options, selected = setOf("n"), onSelectedChange = { changes += it })
        }

        composeRule.onNodeWithText("Teclado").performClick()

        assertEquals(listOf(setOf("n", "t")), changes)
    }

    @Test
    fun clickRemovesTheOption() {
        val changes = mutableListOf<Set<String>>()
        composeRule.setZeraContent {
            SelectBoxList(options = options, selected = setOf("n", "t"), onSelectedChange = { changes += it })
        }

        composeRule.onNodeWithText("Notebook").performClick()

        assertEquals(listOf(setOf("t")), changes)
    }

    @Test
    fun selectAllIsHiddenByDefault() {
        composeRule.setZeraContent {
            SelectBoxList(options = options, selected = emptySet(), onSelectedChange = {})
        }

        composeRule.onNodeWithText(selectAll).assertDoesNotExist()
    }

    @Test
    fun selectAllIsOffWhileAnOptionIsMissing() {
        composeRule.setZeraContent {
            SelectBoxList(options = options, selected = setOf("n"), onSelectedChange = {}, hasSelectAll = true)
        }

        composeRule.onNodeWithText(selectAll).assertIsOff()
    }

    @Test
    fun selectAllIsOnWithEverythingSelected() {
        composeRule.setZeraContent {
            SelectBoxList(options = options, selected = setOf("n", "t"), onSelectedChange = {}, hasSelectAll = true)
        }

        composeRule.onNodeWithText(selectAll).assertIsOn()
    }

    @Test
    fun selectAllKeepsSelectionsOutsideTheList() {
        val changes = mutableListOf<Set<String>>()
        composeRule.setZeraContent {
            SelectBoxList(
                options = options,
                selected = setOf("outra-pagina"),
                onSelectedChange = { changes += it },
                hasSelectAll = true,
            )
        }

        composeRule.onNodeWithText(selectAll).performClick()

        assertEquals(listOf(setOf("outra-pagina", "n", "t")), changes)
    }

    @Test
    fun clearingAllKeepsSelectionsOutsideTheList() {
        val changes = mutableListOf<Set<String>>()
        composeRule.setZeraContent {
            SelectBoxList(
                options = options,
                selected = setOf("outra-pagina", "n", "t"),
                onSelectedChange = { changes += it },
                hasSelectAll = true,
            )
        }

        composeRule.onNodeWithText(selectAll).performClick()

        assertEquals(listOf(setOf("outra-pagina")), changes)
    }

    @Test
    fun emptyListShowsTheDefaultMessageWithoutSelectAll() {
        composeRule.setZeraContent {
            SelectBoxList(options = emptyList(), selected = emptySet(), onSelectedChange = {}, hasSelectAll = true)
        }

        composeRule.onNodeWithText("Nenhuma opção disponível").assertIsDisplayed()
        composeRule.onNodeWithText(selectAll).assertDoesNotExist()
    }

    @Test
    fun emptyListShowsCustomContent() {
        composeRule.setZeraContent {
            SelectBoxList(
                options = emptyList(),
                selected = emptySet(),
                onSelectedChange = {},
                emptyContent = { BodyText(text = "Sem itens") },
            )
        }

        composeRule.onNodeWithText("Sem itens").assertIsDisplayed()
    }

    @Test
    fun loadingMoreShowsAnIndicator() {
        composeRule.setZeraContent {
            SelectBoxList(options = options, selected = emptySet(), onSelectedChange = {}, isLoadingMore = true)
        }

        composeRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)).assertIsDisplayed()
    }
}
