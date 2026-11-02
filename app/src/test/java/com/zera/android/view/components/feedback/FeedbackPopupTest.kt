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
class FeedbackPopupTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun showsTheTitleAndSubtitle() {
        composeRule.setZeraContent {
            FeedbackPopup(title = "Alteração salva!", subtitle = "Seu gestor será notificado", onDismissRequest = {})
        }

        composeRule.onNodeWithText("Alteração salva!").assertIsDisplayed()
        composeRule.onNodeWithText("Seu gestor será notificado").assertIsDisplayed()
    }

    @Test
    fun showsOnlyTheTitleWithoutSubtitle() {
        composeRule.setZeraContent {
            FeedbackPopup(title = "Alteração salva!", onDismissRequest = {})
        }

        composeRule.onNodeWithText("Alteração salva!").assertIsDisplayed()
    }

    @Test
    fun closeButtonCallsOnDismissRequest() {
        var dismissals = 0
        composeRule.setZeraContent {
            FeedbackPopup(title = "Alteração salva!", onDismissRequest = { dismissals++ })
        }

        composeRule.onNodeWithContentDescription("Fechar").performClick()

        assertEquals(1, dismissals)
    }
}
