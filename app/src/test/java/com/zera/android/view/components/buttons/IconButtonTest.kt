package com.zera.android.view.components.buttons

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import com.zera.android.view.components.setZeraContent
import com.zera.android.view.theme.icons.ZeraIcon
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class IconButtonTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun isFoundByTheDescription() {
        composeRule.setZeraContent {
            IconButton(icon = ZeraIcon.Plus, onClick = {}, contentDescription = "Adicionar")
        }

        composeRule.onNodeWithContentDescription("Adicionar").assertIsDisplayed().assertIsEnabled()
    }

    @Test
    fun clickCallsOnClick() {
        var clicks = 0
        composeRule.setZeraContent {
            IconButton(icon = ZeraIcon.Plus, onClick = { clicks++ }, contentDescription = "Adicionar")
        }

        composeRule.onNodeWithContentDescription("Adicionar").performClick()

        assertEquals(1, clicks)
    }

    @Test
    fun disabledIgnoresClicks() {
        var clicks = 0
        composeRule.setZeraContent {
            IconButton(
                icon = ZeraIcon.Plus,
                onClick = { clicks++ },
                contentDescription = "Adicionar",
                enabled = false,
            )
        }

        composeRule.onNodeWithContentDescription("Adicionar").assertIsNotEnabled().performClick()

        assertEquals(0, clicks)
    }
}
