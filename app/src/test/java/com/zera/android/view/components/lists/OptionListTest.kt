package com.zera.android.view.components.lists

import androidx.compose.foundation.layout.Column
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertHasNoClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.zera.android.view.components.setZeraContent
import com.zera.android.view.components.texts.BodyText
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class OptionListTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val models = OptionItem(description = "14 itens", name = "Modelos")
    private val disposals = OptionItem(description = "2 agendados", name = "Descartes", statusText = "Pendente")

    @Test
    fun showsTheOptions() {
        composeRule.setZeraContent {
            OptionList(options = listOf(models, disposals), onItemClick = {})
        }

        composeRule.onNodeWithText("Modelos").assertIsDisplayed()
        composeRule.onNodeWithText("14 itens").assertIsDisplayed()
        composeRule.onNodeWithText("Descartes").assertIsDisplayed()
    }

    @Test
    fun clickSendsTheOption() {
        val clicks = mutableListOf<OptionItem>()
        composeRule.setZeraContent {
            OptionList(options = listOf(models, disposals), onItemClick = { clicks += it })
        }

        composeRule.onNodeWithText("Descartes").performClick()

        assertEquals(listOf(disposals), clicks)
    }

    @Test
    fun showsTheStatusTag() {
        composeRule.setZeraContent {
            OptionList(options = listOf(disposals), onItemClick = {})
        }

        composeRule.onNodeWithText("Pendente").assertIsDisplayed()
    }

    @Test
    fun itemWithoutActionHidesTheStatusTag() {
        composeRule.setZeraContent {
            OptionList(options = listOf(disposals))
        }

        composeRule.onNodeWithText("Pendente").assertDoesNotExist()
    }

    @Test
    fun hidesABlankDescription() {
        composeRule.setZeraContent {
            OptionListItem(itemName = "Modelos", description = "   ", onClick = {})
        }

        composeRule.onNodeWithText("   ").assertDoesNotExist()
    }

    @Test
    fun withoutIconTheNameMovesToTheStart() {
        composeRule.setZeraContent {
            Column {
                OptionListItem(itemName = "Com ícone", description = "", onClick = {})
                OptionListItem(itemName = "Sem ícone", description = "", onClick = {}, hasIcon = false)
            }
        }

        val withIcon = composeRule.onNodeWithText("Com ícone", useUnmergedTree = true).getUnclippedBoundsInRoot()
        val withoutIcon = composeRule.onNodeWithText("Sem ícone", useUnmergedTree = true).getUnclippedBoundsInRoot()
        assertTrue(withoutIcon.left < withIcon.left)
    }

    @Test
    fun emptyListShowsTheDefaultMessage() {
        composeRule.setZeraContent {
            OptionList(options = emptyList())
        }

        composeRule.onNodeWithText("Nenhuma opção encontrada").assertIsDisplayed()
    }

    @Test
    fun emptyListShowsCustomContent() {
        composeRule.setZeraContent {
            OptionList(options = emptyList(), emptyContent = { BodyText(text = "Sem opções") })
        }

        composeRule.onNodeWithText("Sem opções").assertIsDisplayed()
        composeRule.onNodeWithText("Nenhuma opção encontrada").assertDoesNotExist()
    }

    @Test
    fun loadingMoreShowsAnIndicator() {
        composeRule.setZeraContent {
            OptionList(options = listOf(models), isLoadingMore = true)
        }

        composeRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)).assertIsDisplayed()
    }

    @Ignore("Problema conhecido 3 da spec testes-componentes-visuais: o item sem ação continua clicável")
    @Test
    fun itemWithoutActionIsNotClickable() {
        composeRule.setZeraContent {
            OptionList(options = listOf(models))
        }

        composeRule.onNodeWithText("Modelos").assertHasNoClickAction()
    }
}
