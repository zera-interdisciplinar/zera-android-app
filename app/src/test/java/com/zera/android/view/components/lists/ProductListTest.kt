package com.zera.android.view.components.lists

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertHasNoClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.zera.android.view.components.setZeraContent
import com.zera.android.view.components.texts.BodyText
import org.junit.Assert.assertEquals
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ProductListTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val notebook = ProductItem(id = "265964", name = "Notebook")
    private val keyboard = ProductItem(id = "118203", name = "Teclado", statusText = "Pendente")

    @Test
    fun showsTheProducts() {
        composeRule.setZeraContent {
            ProductList(products = listOf(notebook, keyboard), onItemClick = {})
        }

        composeRule.onNodeWithText("Notebook").assertIsDisplayed()
        composeRule.onNodeWithText("ID 265964").assertIsDisplayed()
        composeRule.onNodeWithText("Teclado").assertIsDisplayed()
    }

    @Test
    fun cutsTheIdToSevenCharacters() {
        composeRule.setZeraContent {
            ProductList(products = listOf(ProductItem(id = "FA069700DA28", name = "Notebook")), onItemClick = {})
        }

        composeRule.onNodeWithText("ID FA06970").assertIsDisplayed()
    }

    @Test
    fun clickSendsTheProduct() {
        val clicks = mutableListOf<ProductItem>()
        composeRule.setZeraContent {
            ProductList(products = listOf(notebook, keyboard), onItemClick = { clicks += it })
        }

        composeRule.onNodeWithText("Teclado").performClick()

        assertEquals(listOf(keyboard), clicks)
    }

    @Test
    fun showsTheStatusTag() {
        composeRule.setZeraContent {
            ProductList(products = listOf(keyboard), onItemClick = {})
        }

        composeRule.onNodeWithText("Pendente").assertIsDisplayed()
    }

    @Test
    fun emptyListShowsTheDefaultMessage() {
        composeRule.setZeraContent {
            ProductList(products = emptyList())
        }

        composeRule.onNodeWithText("Nenhum produto encontrado").assertIsDisplayed()
    }

    @Test
    fun emptyListShowsCustomContent() {
        composeRule.setZeraContent {
            ProductList(products = emptyList(), emptyContent = { BodyText(text = "Sem itens") })
        }

        composeRule.onNodeWithText("Sem itens").assertIsDisplayed()
        composeRule.onNodeWithText("Nenhum produto encontrado").assertDoesNotExist()
    }

    @Test
    fun loadingMoreShowsAnIndicator() {
        composeRule.setZeraContent {
            ProductList(products = listOf(notebook), isLoadingMore = true)
        }

        composeRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)).assertIsDisplayed()
    }

    @Test
    fun shortListAsksForTheNextPage() {
        var pages = 0
        composeRule.setZeraContent {
            ProductList(products = listOf(notebook), onEndReached = { pages++ })
        }

        composeRule.waitForIdle()

        assertEquals(1, pages)
    }

    @Ignore("Problema conhecido 3 da spec testes-componentes-visuais: o item sem ação continua clicável")
    @Test
    fun itemWithoutActionIsNotClickable() {
        composeRule.setZeraContent {
            ProductList(products = listOf(notebook))
        }

        composeRule.onNodeWithText("Notebook").assertHasNoClickAction()
    }
}
