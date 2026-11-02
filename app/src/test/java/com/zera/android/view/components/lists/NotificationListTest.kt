package com.zera.android.view.components.lists

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.zera.android.view.components.setZeraContent
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class NotificationListTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val resolvable = NotificationItem(id = "1", label = "5 produtos sem classificação", resolvable = true)
    private val fixed = NotificationItem(id = "2", label = "Item aprovado")

    @Test
    fun showsTheNotifications() {
        composeRule.setZeraContent {
            NotificationList(notifications = listOf(resolvable, fixed))
        }

        composeRule.onNodeWithText("5 produtos sem classificação").assertIsDisplayed()
        composeRule.onNodeWithText("Item aprovado").assertIsDisplayed()
    }

    @Test
    fun actionNeedsAResolvableItem() {
        composeRule.setZeraContent {
            NotificationList(notifications = listOf(fixed), onItemClick = {})
        }

        composeRule.onNodeWithText("Resolver agora").assertDoesNotExist()
    }

    @Test
    fun actionNeedsOnItemClick() {
        composeRule.setZeraContent {
            NotificationList(notifications = listOf(resolvable))
        }

        composeRule.onNodeWithText("Resolver agora").assertDoesNotExist()
    }

    @Test
    fun actionSendsTheNotification() {
        val clicks = mutableListOf<NotificationItem>()
        composeRule.setZeraContent {
            NotificationList(notifications = listOf(resolvable, fixed), onItemClick = { clicks += it })
        }

        composeRule.onNodeWithText("Resolver agora").performClick()

        assertEquals(listOf(resolvable), clicks)
    }

    @Test
    fun usesTheActionLabelOfTheItem() {
        composeRule.setZeraContent {
            NotificationList(
                notifications = listOf(resolvable.copy(actionLabel = "Reenviar convite")),
                onItemClick = {},
            )
        }

        composeRule.onNodeWithText("Reenviar convite").assertIsDisplayed()
    }

    @Test
    fun fixedListHasNoCloseButton() {
        composeRule.setZeraContent {
            NotificationList(notifications = listOf(fixed))
        }

        composeRule.onNodeWithContentDescription("Dispensar alerta").assertDoesNotExist()
    }

    @Test
    fun closeButtonSendsTheNotification() {
        val dismissed = mutableListOf<NotificationItem>()
        composeRule.setZeraContent {
            NotificationList(notifications = listOf(fixed), onItemDismiss = { dismissed += it })
        }

        composeRule.onNodeWithContentDescription("Dispensar alerta").performClick()

        assertEquals(listOf(fixed), dismissed)
    }

    @Test
    fun emptyListShowsTheDefaultMessage() {
        composeRule.setZeraContent {
            NotificationList(notifications = emptyList())
        }

        composeRule.onNodeWithText("Nenhuma notificação").assertIsDisplayed()
    }
}
