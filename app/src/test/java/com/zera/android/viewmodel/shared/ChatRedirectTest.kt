package com.zera.android.viewmodel.shared

import com.zera.android.model.entity.user.UserRole
import com.zera.android.view.navigation.Route
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ChatRedirectTest {
    @Test
    fun recyclingIsAllowedForBothRoles() {
        assertEquals(
            Route.Recycling to "Ver pontos próximos",
            ChatRedirect.resolve("Recycling", "Ver pontos próximos", UserRole.MANAGER),
        )
        assertEquals(
            Route.Recycling to "Ver pontos próximos",
            ChatRedirect.resolve("Recycling", "Ver pontos próximos", UserRole.EMPLOYEE),
        )
    }

    @Test
    fun scanIsHiddenFromManager() {
        assertNull(ChatRedirect.resolve("Scan", "Escanear código de barras", UserRole.MANAGER))
        assertEquals(
            Route.Scan to "Escanear código de barras",
            ChatRedirect.resolve("Scan", "Escanear código de barras", UserRole.EMPLOYEE),
        )
    }

    @Test
    fun parameterizedAndUnknownScreensAreIgnored() {
        assertNull(ChatRedirect.resolve("ItemDetails", "Ver item", UserRole.MANAGER))
        assertNull(ChatRedirect.resolve("DoesNotExist", "Ir", UserRole.MANAGER))
        assertNull(ChatRedirect.resolve(null, "Ir", UserRole.MANAGER))
        assertNull(ChatRedirect.resolve("Recycling", "  ", UserRole.MANAGER))
    }

    @Test
    fun chatAndAuthScreensAreNeverLinked() {
        assertNull(ChatRedirect.resolve("ChatBot", "Abrir chat", UserRole.MANAGER))
        assertNull(ChatRedirect.resolve("ChatConversations", "Inbox", UserRole.MANAGER))
        assertNull(ChatRedirect.resolve("SignIn", "Login", UserRole.MANAGER))
    }
}
