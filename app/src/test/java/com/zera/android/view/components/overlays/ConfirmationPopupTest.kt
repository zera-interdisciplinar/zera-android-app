package com.zera.android.view.components.overlays

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
class ConfirmationPopupTest {
    @get:Rule
    val composeRule = createComposeRule()

    private var confirmations = 0
    private var dismissals = 0

    private fun showPopup() {
        composeRule.setZeraContent {
            ConfirmationPopup(
                title = "Excluir esse item?",
                message = "Essa ação não pode ser desfeita.",
                confirmButtonText = "Excluir",
                onConfirm = { confirmations++ },
                onDismissRequest = { dismissals++ },
            )
        }
    }

    @Test
    fun showsTheTexts() {
        showPopup()

        composeRule.onNodeWithText("Excluir esse item?").assertIsDisplayed()
        composeRule.onNodeWithText("Essa ação não pode ser desfeita.").assertIsDisplayed()
        composeRule.onNodeWithText("Excluir").assertIsDisplayed()
        composeRule.onNodeWithText("Cancelar").assertIsDisplayed()
    }

    @Test
    fun confirmCallsOnlyOnConfirm() {
        showPopup()

        composeRule.onNodeWithText("Excluir").performClick()

        assertEquals(1, confirmations)
        assertEquals(0, dismissals)
    }

    @Test
    fun cancelCallsOnlyOnDismissRequest() {
        showPopup()

        composeRule.onNodeWithText("Cancelar").performClick()

        assertEquals(0, confirmations)
        assertEquals(1, dismissals)
    }

    @Test
    fun hasNoCloseButton() {
        showPopup()

        composeRule.onNodeWithContentDescription("Fechar").assertDoesNotExist()
    }
}
