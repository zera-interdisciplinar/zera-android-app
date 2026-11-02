package com.zera.android.view.components.cards

import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import com.zera.android.view.components.setZeraContent
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DismissibleNotificationTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val label = "Garantia vencendo"
    private var dismissals = 0

    /** Maior que o `SwipeResetDelay` do componente. */
    private val afterSwipeReset = 1_500L

    private fun showCard() {
        composeRule.setZeraContent {
            DismissibleNotification(label = label, onDismiss = { dismissals++ })
        }
    }

    private fun cardLeft() =
        composeRule.onNodeWithText(label, useUnmergedTree = true).getUnclippedBoundsInRoot().left

    @Test
    fun closeButtonCallsOnDismiss() {
        showCard()

        composeRule.onNodeWithContentDescription("Dispensar alerta").performClick()

        assertEquals(1, dismissals)
    }

    @Test
    fun swipingLeftCallsOnDismiss() {
        showCard()

        composeRule.onNodeWithText(label).performTouchInput { swipeLeft() }
        composeRule.waitForIdle()

        assertEquals(1, dismissals)
    }

    @Test
    fun swipingRightCallsOnDismiss() {
        showCard()

        composeRule.onNodeWithText(label).performTouchInput { swipeRight() }
        composeRule.waitForIdle()

        assertEquals(1, dismissals)
    }

    @Test
    fun cardStillOnScreenReturnsToItsPlace() {
        showCard()
        val initialLeft = cardLeft()

        composeRule.onNodeWithText(label).performTouchInput { swipeLeft() }
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeBy(afterSwipeReset)
        composeRule.waitForIdle()

        assertEquals(initialLeft, cardLeft())
        assertEquals(1, dismissals)
    }

    @Test
    fun cardCanBeSwipedAgainAfterReturning() {
        showCard()

        composeRule.onNodeWithText(label).performTouchInput { swipeLeft() }
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeBy(afterSwipeReset)
        composeRule.waitForIdle()
        composeRule.onNodeWithText(label).performTouchInput { swipeLeft() }
        composeRule.waitForIdle()

        assertEquals(2, dismissals)
    }
}
