package com.zera.android.view.components.lists

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.zera.android.view.components.setZeraContent
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class InviteListTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val first = InviteItem(code = "743731", inviteeName = "Genivaldo", expiresInHours = 12)
    private val second = InviteItem(code = "443729", inviteeName = "Pedro", expiresInHours = 107)

    @Test
    fun showsTheInviteData() {
        composeRule.setZeraContent {
            InviteList(invites = listOf(first), onCopyCodeClick = {})
        }

        composeRule.onNodeWithText("Código 743731").assertIsDisplayed()
        composeRule.onNodeWithText("Genivaldo").assertIsDisplayed()
        composeRule.onNodeWithText("Expira em 12h ·").assertIsDisplayed()
    }

    @Test
    fun copySendsTheInvite() {
        val copies = mutableListOf<InviteItem>()
        composeRule.setZeraContent {
            InviteList(invites = listOf(first, second), onCopyCodeClick = { copies += it })
        }

        composeRule.onAllNodesWithText("Copiar código")[1].performClick()

        assertEquals(listOf(second), copies)
    }

    @Test
    fun emptyListShowsTheDefaultMessage() {
        composeRule.setZeraContent {
            InviteList(invites = emptyList(), onCopyCodeClick = {})
        }

        composeRule.onNodeWithText("Nenhum convite pendente").assertIsDisplayed()
    }
}
