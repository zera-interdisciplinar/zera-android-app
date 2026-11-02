package com.zera.android.view.components.overlays

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.zera.android.view.components.setZeraContent
import com.zera.android.view.components.texts.BodyText
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BottomSheetTest {
    @get:Rule
    val composeRule = createComposeRule()

    @OptIn(ExperimentalMaterial3Api::class)
    @Test
    fun showsTheContent() {
        composeRule.setZeraContent {
            BottomSheet(onDismissRequest = {}) {
                BodyText(text = "Conteúdo do sheet")
            }
        }

        composeRule.onNodeWithText("Conteúdo do sheet").assertIsDisplayed()
    }
}
