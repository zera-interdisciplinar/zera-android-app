package com.zera.android.view.components.feedback

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.zera.android.view.components.setZeraContent
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ErrorFeedbackPopupTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun showsTheFixedTitleAndDescription() {
        composeRule.setZeraContent {
            ErrorFeedbackPopup(description = "Não conseguimos carregar os itens.", onDismissRequest = {})
        }

        composeRule.onNodeWithText("Algo deu errado").assertIsDisplayed()
        composeRule.onNodeWithText("Não conseguimos carregar os itens.").assertIsDisplayed()
    }

    @Test
    fun closeButtonCallsOnDismissRequest() {
        var dismissals = 0
        composeRule.setZeraContent {
            ErrorFeedbackPopup(description = "Não conseguimos carregar os itens.", onDismissRequest = { dismissals++ })
        }

        composeRule.onNodeWithContentDescription("Fechar").performClick()

        assertEquals(1, dismissals)
    }
}
