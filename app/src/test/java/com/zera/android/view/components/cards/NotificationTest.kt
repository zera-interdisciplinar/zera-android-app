package com.zera.android.view.components.cards

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
class NotificationTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun showsTheLabelAndText() {
        composeRule.setZeraContent {
            Notification(label = "Recusado pelo gestor", text = "Chip controlador")
        }

        composeRule.onNodeWithText("Recusado pelo gestor").assertIsDisplayed()
        composeRule.onNodeWithText("Chip controlador").assertIsDisplayed()
    }

    @Test
    fun withoutRedirectHasNoAction() {
        composeRule.setZeraContent {
            Notification(label = "Recusado pelo gestor")
        }

        composeRule.onNodeWithText("Resolver agora").assertDoesNotExist()
    }

    @Test
    fun actionCallsRedirect() {
        var redirects = 0
        composeRule.setZeraContent {
            Notification(label = "Completar cadastro", redirect = { redirects++ })
        }

        composeRule.onNodeWithText("Resolver agora").performClick()

        assertEquals(1, redirects)
    }

    @Test
    fun usesTheCustomActionLabel() {
        composeRule.setZeraContent {
            Notification(label = "Convite expira hoje", redirect = {}, redirectLabel = "Reenviar convite")
        }

        composeRule.onNodeWithText("Reenviar convite").assertIsDisplayed()
        composeRule.onNodeWithText("Resolver agora").assertDoesNotExist()
    }

    @Test
    fun withoutOnDismissHasNoCloseButton() {
        composeRule.setZeraContent {
            Notification(label = "Recusado pelo gestor")
        }

        composeRule.onNodeWithContentDescription("Dispensar alerta").assertDoesNotExist()
    }

    @Test
    fun closeButtonCallsOnDismiss() {
        var dismissals = 0
        composeRule.setZeraContent {
            Notification(label = "Item aprovado", onDismiss = { dismissals++ })
        }

        composeRule.onNodeWithContentDescription("Dispensar alerta").performClick()

        assertEquals(1, dismissals)
    }
}
