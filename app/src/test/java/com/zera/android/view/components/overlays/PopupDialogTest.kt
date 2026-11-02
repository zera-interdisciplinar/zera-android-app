package com.zera.android.view.components.overlays

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
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
class PopupDialogTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun showsTheContent() {
        composeRule.setZeraContent {
            PopupDialog(onDismissRequest = {}) {
                BodyText(text = "Conteúdo do popup")
            }
        }

        composeRule.onNodeWithText("Conteúdo do popup").assertIsDisplayed()
    }

    @Test
    fun closeButtonCallsOnDismissRequest() {
        var dismissals = 0
        composeRule.setZeraContent {
            PopupDialog(onDismissRequest = { dismissals++ }) {
                BodyText(text = "Conteúdo do popup")
            }
        }

        composeRule.onNodeWithContentDescription("Fechar").performClick()

        assertEquals(1, dismissals)
    }

    @Test
    fun hidesTheCloseButton() {
        composeRule.setZeraContent {
            PopupDialog(onDismissRequest = {}, hasDismissButton = false) {
                BodyText(text = "Conteúdo do popup")
            }
        }

        composeRule.onNodeWithContentDescription("Fechar").assertDoesNotExist()
    }
}
