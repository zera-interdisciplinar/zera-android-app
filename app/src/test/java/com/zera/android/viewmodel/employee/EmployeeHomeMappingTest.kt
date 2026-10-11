package com.zera.android.viewmodel.employee

import com.zera.android.model.dto.inventory.DashboardHomeResponseDTO
import com.zera.android.model.dto.inventory.PendingItemResponseDTO
import com.zera.android.model.dto.inventory.WorkCenterResponseDTO
import com.zera.android.model.entity.user.UserRole
import com.zera.android.view.components.lists.NotificationItem
import com.zera.android.view.theme.ZeraColorFamily
import org.junit.Assert.assertEquals
import org.junit.Test

class EmployeeHomeMappingTest {
    @Test
    fun occupationConvertsPercentToFraction() {
        val home = DashboardHomeResponseDTO(
            activeItems = 128,
            occupancyPercent = 64.0,
        )
        assertEquals(0.64f, EmployeeHomeViewModel.occupationFrom(home), 0.0001f)
    }

    @Test
    fun occupationIsZeroWhenCapacityIsMissing() {
        val home = DashboardHomeResponseDTO(activeItems = 10, occupancyPercent = null)
        assertEquals(0f, EmployeeHomeViewModel.occupationFrom(home), 0.0f)
    }

    @Test
    fun roleLabelMapsEmployee() {
        assertEquals("Gestor", EmployeeHomeViewModel.roleLabel(UserRole.MANAGER))
        assertEquals("Operário", EmployeeHomeViewModel.roleLabel(UserRole.EMPLOYEE))
    }

    @Test
    fun pendenciesSkipEmptyBuckets() {
        val workCenter = WorkCenterResponseDTO()
        assertEquals(emptyList<NotificationItem>(), EmployeeHomeViewModel.pendenciesFrom(workCenter))
    }

    @Test
    fun pendenciesUseSingularAndPluralLabels() {
        val oneDraft = WorkCenterResponseDTO(
            drafts = listOf(PendingItemResponseDTO(id = "d1", name = "A")),
        )
        val many = WorkCenterResponseDTO(
            drafts = listOf(
                PendingItemResponseDTO(id = "d1", name = "A"),
                PendingItemResponseDTO(id = "d2", name = "B"),
                PendingItemResponseDTO(id = "d3", name = "C"),
            ),
            rejected = listOf(PendingItemResponseDTO(id = "r1", name = "R")),
            damagedWithoutDestination = listOf(
                PendingItemResponseDTO(id = "x1", name = "X"),
                PendingItemResponseDTO(id = "x2", name = "Y"),
            ),
        )

        val one = EmployeeHomeViewModel.pendenciesFrom(oneDraft)
        assertEquals("incomplete-registrations", one.single().id)
        assertEquals("1 cadastro incompleto", one.single().label)
        assertEquals(ZeraColorFamily.Red, one.single().style)

        val cards = EmployeeHomeViewModel.pendenciesFrom(many)
        assertEquals(
            listOf("incomplete-registrations", "rejected-registrations", "damaged-without-destination"),
            cards.map { it.id },
        )
        assertEquals("3 cadastros incompletos", cards[0].label)
        assertEquals("1 cadastro recusado", cards[1].label)
        assertEquals("2 itens danificados sem destino", cards[2].label)
        assertEquals(ZeraColorFamily.Red, cards[1].style)
        assertEquals(ZeraColorFamily.Yellow, cards[2].style)
    }
}
