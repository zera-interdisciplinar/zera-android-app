package com.zera.android.viewmodel.manager

import com.zera.android.model.dto.inventory.DashboardHomeResponseDTO
import com.zera.android.model.dto.notification.AlertResponseDTO
import com.zera.android.model.entity.user.UserRole
import com.zera.android.view.theme.ZeraColorFamily
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ManagerHomeMappingTest {
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
    fun alertsUseApiDescriptionInListOrder() {
        val alerts = ManagerHomeViewModel.alertsFrom(
            listOf(
                alert(alertId = "first", description = "Estoque alto", severity = "HIGH"),
                alert(alertId = "second", description = "Item aprovado", severity = "LOW"),
            ),
        )
        assertEquals(2, alerts.size)
        assertEquals("first", alerts[0].id)
        assertEquals("Estoque alto", alerts[0].label)
        assertEquals(ZeraColorFamily.Red, alerts[0].style)
        assertEquals("second", alerts[1].id)
        assertEquals("Item aprovado", alerts[1].label)
        assertEquals(ZeraColorFamily.Green, alerts[1].style)
    }

    @Test
    fun alertsAreEmptyWhenApiReturnsNone() {
        assertTrue(ManagerHomeViewModel.alertsFrom(emptyList()).isEmpty())
    }

    @Test
    fun severityMapsToColorFamily() {
        assertEquals(ZeraColorFamily.Red, ManagerHomeViewModel.colorFamilyForSeverity("HIGH"))
        assertEquals(ZeraColorFamily.Yellow, ManagerHomeViewModel.colorFamilyForSeverity("MEDIUM"))
        assertEquals(ZeraColorFamily.Green, ManagerHomeViewModel.colorFamilyForSeverity("LOW"))
        assertEquals(ZeraColorFamily.Yellow, ManagerHomeViewModel.colorFamilyForSeverity("UNKNOWN"))
    }

    @Test
    fun roleLabelMapsManager() {
        assertEquals("Gestor", ManagerHomeViewModel.roleLabel(UserRole.MANAGER))
        assertEquals("Operário", ManagerHomeViewModel.roleLabel(UserRole.EMPLOYEE))
    }

    private fun alert(
        alertId: String,
        description: String,
        severity: String,
    ) = AlertResponseDTO(
        alertId = alertId,
        kind = "STOCK_QUANTITY_LIMIT",
        severity = severity,
        status = "OPEN",
        description = description,
        unitId = "unit",
        occurredAt = "2026-09-20T03:15:00",
        createdAt = "2026-09-20T03:15:01",
        updatedAt = "2026-09-20T03:15:01",
    )
}
