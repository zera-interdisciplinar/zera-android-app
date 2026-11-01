package com.zera.android.viewmodel.main.home.manager

import com.zera.android.model.dto.inventory.DashboardHomeResponseDTO
import com.zera.android.model.dto.notification.AlertResponseDTO
import com.zera.android.model.entity.user.UserRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ManagerHomeMappingTest {
    @Test
    fun homeShowsOnlyFirstThreeAlerts() {
        val alerts = (1..5).map { index ->
            AlertResponseDTO(
                alertId = "alert-$index",
                kind = "STOCK_QUANTITY_LIMIT",
                severity = "HIGH",
                status = "OPEN",
                description = "Alerta $index",
                unitId = "unit",
                occurredAt = "2026-09-20T03:15:00",
                createdAt = "2026-09-20T03:15:01",
                updatedAt = "2026-09-20T03:15:01",
            )
        }
        val notifications = ManagerHomeViewModel.homeAlertsFrom(alerts)
        assertEquals(listOf("alert-1", "alert-2", "alert-3"), notifications.map { it.id })
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
