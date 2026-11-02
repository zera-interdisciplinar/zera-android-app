package com.zera.android.view.components.buttons

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasProgressBarRangeInfo
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
class ZeraButtonTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val loadingIndicator = hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)

    @Test
    fun showsTheText() {
        composeRule.setZeraContent {
            ZeraButton(text = "Continuar", onClick = {})
        }

        composeRule.onNodeWithText("Continuar").assertIsDisplayed().assertIsEnabled()
        composeRule.onNode(loadingIndicator).assertDoesNotExist()
    }

    @Test
    fun clickCallsOnClick() {
        var clicks = 0
        composeRule.setZeraContent {
            ZeraButton(text = "Continuar", onClick = { clicks++ })
        }

        composeRule.onNodeWithText("Continuar").performClick()

        assertEquals(1, clicks)
    }

    @Test
    fun disabledIgnoresClicks() {
        var clicks = 0
        composeRule.setZeraContent {
            ZeraButton(text = "Continuar", onClick = { clicks++ }, enabled = false)
        }

        composeRule.onNodeWithText("Continuar").assertIsNotEnabled().performClick()

        assertEquals(0, clicks)
    }

    @Test
    fun loadingReplacesTheTextWithAnIndicator() {
        composeRule.setZeraContent {
            ZeraButton(text = "Continuar", onClick = {}, loading = true)
        }

        composeRule.onNodeWithText("Continuar").assertDoesNotExist()
        composeRule.onNode(loadingIndicator).assertIsDisplayed()
    }

    @Test
    fun loadingIgnoresClicks() {
        var clicks = 0
        composeRule.setZeraContent {
            ZeraButton(text = "Continuar", onClick = { clicks++ }, loading = true)
        }

        composeRule.onNode(hasClickAction()).assertIsNotEnabled().performClick()

        assertEquals(0, clicks)
    }
}
