package com.zera.android.view.components.layouts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertLeftPositionInRootIsEqualTo
import androidx.compose.ui.test.assertTopPositionInRootIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zera.android.view.components.setZeraContent
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Os filhos têm largura fixa para o resultado não depender da fonte renderizada. */
@RunWith(RobolectricTestRunner::class)
class AdaptiveRowTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Composable
    private fun Child(tag: String, width: Dp) {
        Box(modifier = Modifier.testTag(tag).width(width).height(20.dp))
    }

    @Test
    fun childrenThatFitShareTheRowEqually() {
        composeRule.setZeraContent {
            Box(modifier = Modifier.width(300.dp)) {
                AdaptiveRow(spacing = 20.dp) {
                    Child(tag = "first", width = 100.dp)
                    Child(tag = "second", width = 50.dp)
                }
            }
        }

        composeRule.onNodeWithTag("first")
            .assertLeftPositionInRootIsEqualTo(0.dp)
            .assertTopPositionInRootIsEqualTo(0.dp)
            .assertWidthIsEqualTo(140.dp)
        composeRule.onNodeWithTag("second")
            .assertLeftPositionInRootIsEqualTo(160.dp)
            .assertTopPositionInRootIsEqualTo(0.dp)
            .assertWidthIsEqualTo(140.dp)
    }

    @Test
    fun aChildThatDoesNotFitStacksAllOfThem() {
        composeRule.setZeraContent {
            Box(modifier = Modifier.width(300.dp)) {
                AdaptiveRow(spacing = 20.dp, verticalSpacing = 10.dp) {
                    Child(tag = "first", width = 200.dp)
                    Child(tag = "second", width = 50.dp)
                }
            }
        }

        composeRule.onNodeWithTag("first")
            .assertLeftPositionInRootIsEqualTo(0.dp)
            .assertTopPositionInRootIsEqualTo(0.dp)
            .assertWidthIsEqualTo(300.dp)
        composeRule.onNodeWithTag("second")
            .assertLeftPositionInRootIsEqualTo(0.dp)
            .assertTopPositionInRootIsEqualTo(30.dp)
            .assertWidthIsEqualTo(300.dp)
    }

    @Test
    fun aChildExactlyAtItsShareStaysInTheRow() {
        composeRule.setZeraContent {
            Box(modifier = Modifier.width(300.dp)) {
                AdaptiveRow(spacing = 20.dp) {
                    Child(tag = "first", width = 140.dp)
                    Child(tag = "second", width = 140.dp)
                }
            }
        }

        composeRule.onNodeWithTag("second").assertTopPositionInRootIsEqualTo(0.dp)
    }

    @Test
    fun rendersWithoutChildren() {
        composeRule.setZeraContent {
            AdaptiveRow {}
        }

        composeRule.onNodeWithTag("first").assertDoesNotExist()
    }
}
