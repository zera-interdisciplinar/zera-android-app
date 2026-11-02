package com.zera.android.view.components.inputs

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.isSelectable
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
class ZeraRadioButtonTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun showsTheSelectedState() {
        composeRule.setZeraContent {
            ZeraRadioButton(selected = true, onClick = {})
        }

        composeRule.onNode(isSelectable()).assertIsSelected()
    }

    @Test
    fun showsTheUnselectedState() {
        composeRule.setZeraContent {
            ZeraRadioButton(selected = false, onClick = {})
        }

        composeRule.onNode(isSelectable()).assertIsNotSelected()
    }

    @Test
    fun clickCallsOnClick() {
        var clicks = 0
        composeRule.setZeraContent {
            ZeraRadioButton(selected = false, onClick = { clicks++ })
        }

        composeRule.onNode(isSelectable()).performClick()

        assertEquals(1, clicks)
    }

    @Test
    fun showsTheLabel() {
        composeRule.setZeraContent {
            ZeraRadioButton(selected = false, onClick = {}, label = "Possui danos?")
        }

        composeRule.onNodeWithText("Possui danos?").assertIsDisplayed()
    }
}
