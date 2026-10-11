package com.zera.android.viewmodel.employee

import com.zera.android.model.dto.inventory.DashboardHomeResponseDTO
import com.zera.android.model.entity.user.UserRole
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
}
