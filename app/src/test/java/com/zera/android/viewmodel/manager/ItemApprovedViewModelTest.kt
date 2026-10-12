package com.zera.android.viewmodel.manager

import com.zera.android.view.components.outros.ItemStatus
import com.zera.android.view.navigation.NavCommand
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ItemApprovedViewModelTest {
    @Before
    fun drainPendingNavCommands() = runBlocking {
        while (withTimeoutOrNull(50) { ZeraNavigator.commands.first() } != null) {
            // esvazia o canal compartilhado entre testes
        }
    }

    @Test
    fun viewPendingItemsOpensCatalogWithPendingApprovalFilter() = runBlocking {
        ItemApprovedViewModel().onViewPendingItemsClick()

        assertEquals(
            NavCommand.Navigate(Route.Itens(status = ItemStatus.PendingApproval.label)),
            withTimeout(1_000) { ZeraNavigator.commands.first() },
        )
    }
}
