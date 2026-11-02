package com.zera.android.view.components.inputs

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertRangeInfoEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performSemanticsAction
import com.zera.android.view.components.setZeraContent
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ZeraSliderInputTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val slider = SemanticsMatcher.keyIsDefined(SemanticsActions.SetProgress)

    @Test
    fun showsTheLabelAndCurrentValue() {
        composeRule.setZeraContent {
            ZeraSliderInput(value = 4, onValueChange = {}, label = "Intensidade de uso")
        }

        composeRule.onNodeWithText("INTENSIDADE DE USO").assertIsDisplayed()
        composeRule.onNodeWithText("4").assertIsDisplayed()
    }

    @Test
    fun showsTheRangeLimits() {
        composeRule.setZeraContent {
            ZeraSliderInput(value = 4, onValueChange = {}, valueRange = 2..8)
        }

        composeRule.onNodeWithText("2").assertIsDisplayed()
        composeRule.onNodeWithText("8").assertIsDisplayed()
    }

    @Test
    fun hasAStopForEveryInteger() {
        composeRule.setZeraContent {
            ZeraSliderInput(value = 5, onValueChange = {})
        }

        composeRule.onNode(slider).assertRangeInfoEquals(ProgressBarRangeInfo(5f, 1f..10f, 8))
    }

    @Test
    fun changingTheProgressSendsAnInteger() {
        val changes = mutableListOf<Int>()
        composeRule.setZeraContent {
            ZeraSliderInput(value = 5, onValueChange = { changes += it })
        }

        composeRule.onNode(slider).performSemanticsAction(SemanticsActions.SetProgress) { it(7f) }

        assertEquals(listOf(7), changes)
    }

    @Test
    fun disabledIsNotEnabled() {
        composeRule.setZeraContent {
            ZeraSliderInput(value = 5, onValueChange = {}, enabled = false)
        }

        composeRule.onNode(slider).assertIsNotEnabled()
    }
}
