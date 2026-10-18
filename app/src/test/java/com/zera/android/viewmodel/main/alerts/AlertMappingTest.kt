package com.zera.android.viewmodel.main.alerts

import com.zera.android.model.dto.notification.AlertResponseDTO
import com.zera.android.view.theme.ZeraColorFamily
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AlertMappingTest {
    @Test
    fun alertsUseApiDescriptionInListOrder() {
        val alerts = AlertMapping.notificationsFrom(
            listOf(
                alert(alertId = "first", description = "Estoque alto", severity = "HIGH"),
                alert(alertId = "second", description = "Item aprovado", severity = "LOW"),
            ),
        )
        assertEquals(2, alerts.size)
        assertEquals("first", alerts[0].id)
        assertEquals("Estoque alto", alerts[0].label)
        assertEquals(ZeraColorFamily.Blue, alerts[0].style)
        assertEquals("second", alerts[1].id)
        assertEquals("Item aprovado", alerts[1].label)
        assertEquals(ZeraColorFamily.Green, alerts[1].style)
        assertTrue(alerts[0].resolvable)
        assertFalse(alerts[1].resolvable)
    }

    @Test
    fun alertsAreEmptyWhenApiReturnsNone() {
        assertTrue(AlertMapping.notificationsFrom(emptyList()).isEmpty())
    }

    @Test
    fun kindMapsToColorFamily() {
        assertEquals(ZeraColorFamily.Green, AlertMapping.colorFamilyForAlert("ITEM_APPROVED", "HIGH"))
        assertEquals(ZeraColorFamily.Yellow, AlertMapping.colorFamilyForAlert("ITEM_PENDING_APPROVAL", "LOW"))
        assertEquals(ZeraColorFamily.Red, AlertMapping.colorFamilyForAlert("ITEM_REJECTED", "LOW"))
        assertEquals(ZeraColorFamily.Blue, AlertMapping.colorFamilyForAlert("STOCK_QUANTITY_LIMIT", "HIGH"))
        assertEquals(ZeraColorFamily.Red, AlertMapping.colorFamilyForAlert("RECYCLABLE_TO_LANDFILL", "LOW"))
    }

    @Test
    fun unknownKindFallsBackToSeverity() {
        assertEquals(ZeraColorFamily.Red, AlertMapping.colorFamilyForAlert("UNKNOWN", "HIGH"))
        assertEquals(ZeraColorFamily.Yellow, AlertMapping.colorFamilyForAlert("UNKNOWN", "MEDIUM"))
        assertEquals(ZeraColorFamily.Green, AlertMapping.colorFamilyForAlert("UNKNOWN", "LOW"))
        assertEquals(ZeraColorFamily.Yellow, AlertMapping.colorFamilyForAlert("UNKNOWN", "OTHER"))
    }

    private fun alert(
        alertId: String,
        description: String,
        severity: String,
        kind: String = if (alertId == "second") "ITEM_APPROVED" else "STOCK_QUANTITY_LIMIT",
    ) = AlertResponseDTO(
        alertId = alertId,
        kind = kind,
        severity = severity,
        status = "OPEN",
        description = description,
        unitId = "unit",
        occurredAt = "2026-09-20T03:15:00",
        createdAt = "2026-09-20T03:15:01",
        updatedAt = "2026-09-20T03:15:01",
    )
}
