package com.zera.android.viewmodel.main.home.manager

import com.zera.android.model.dto.inventory.DashboardHomeResponseDTO
import com.zera.android.model.entity.user.UserRole
import com.zera.android.view.components.lists.NotificationItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ManagerHomeMappingTest {
    @Test
    fun homeShowsOnlyFirstThreeAlerts() {
        val notifications = ManagerHomeViewModel.homeAlertsFrom(fiveAlerts(), emptySet())
        assertEquals(listOf("alert-1", "alert-2", "alert-3"), notifications.map { it.id })
    }

    @Test
    fun nextAlertMovesUpWhenOneIsDismissed() {
        val notifications = ManagerHomeViewModel.homeAlertsFrom(fiveAlerts(), setOf("alert-2"))
        assertEquals(listOf("alert-1", "alert-3", "alert-4"), notifications.map { it.id })
    }

    private fun fiveAlerts() = (1..5).map { index ->
        NotificationItem(id = "alert-$index", label = "Alerta $index")
    }

    @Test
    fun occupationConvertsPercentToFraction() {
        val home = DashboardHomeResponseDTO(
            activeItems = 128,
            occupancyPercent = 64.0,
        )
        assertEquals(0.64f, ManagerHomeViewModel.occupationFrom(home), 0.0001f)
    }

    @Test
    fun occupationIsZeroWhenCapacityIsMissing() {
        val home = DashboardHomeResponseDTO(activeItems = 10, occupancyPercent = null)
        assertEquals(0f, ManagerHomeViewModel.occupationFrom(home), 0.0f)
    }

    @Test
    fun changePercentKeepsArrowAndSign() {
        assertTrue(ManagerHomeViewModel.formatChangePercent(4.35)!!.startsWith("↑"))
        assertTrue(ManagerHomeViewModel.formatChangePercent(-12.0)!!.startsWith("↓"))
        assertNull(ManagerHomeViewModel.formatChangePercent(null))
    }

    @Test
    fun roleLabelMapsManager() {
        assertEquals("Gestor", ManagerHomeViewModel.roleLabel(UserRole.MANAGER))
        assertEquals("Operário", ManagerHomeViewModel.roleLabel(UserRole.EMPLOYEE))
    }
}
