package com.zera.android.view.components.outros

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.zera.android.view.components.setZeraContent
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TagTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun showsTheText() {
        composeRule.setZeraContent {
            Tag(text = "Em aprovação")
        }

        composeRule.onNodeWithText("Em aprovação").assertIsDisplayed()
    }
}
