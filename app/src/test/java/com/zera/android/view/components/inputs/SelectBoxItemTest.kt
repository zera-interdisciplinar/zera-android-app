package com.zera.android.view.components.inputs

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
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
class SelectBoxItemTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun showsTheCheckedState() {
        composeRule.setZeraContent {
            SelectBoxItem(name = "Notebook", selected = true, onClick = {})
        }

        composeRule.onNodeWithText("Notebook").assertIsOn()
    }

    @Test
    fun showsTheUncheckedState() {
        composeRule.setZeraContent {
            SelectBoxItem(name = "Notebook", selected = false, onClick = {})
        }

        composeRule.onNodeWithText("Notebook").assertIsOff()
    }

    @Test
    fun clickCallsOnClick() {
        var clicks = 0
        composeRule.setZeraContent {
            SelectBoxItem(name = "Notebook", selected = false, onClick = { clicks++ })
        }

        composeRule.onNodeWithText("Notebook").performClick()

        assertEquals(1, clicks)
    }

    @Test
    fun showsTheDescription() {
        composeRule.setZeraContent {
            SelectBoxItem(name = "Notebook", description = "ID 842190", selected = false, onClick = {})
        }

        composeRule.onNodeWithText("ID 842190").assertIsDisplayed()
    }
}
