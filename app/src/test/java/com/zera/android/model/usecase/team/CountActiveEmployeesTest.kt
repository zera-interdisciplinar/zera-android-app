package com.zera.android.model.usecase.team

import com.zera.android.model.dto.user.ManagerEmployeeCountDTO
import org.junit.Assert.assertEquals
import org.junit.Test

class CountActiveEmployeesTest {
    @Test
    fun picksLoggedManagerRow() {
        val counts = listOf(
            ManagerEmployeeCountDTO("other", 9),
            ManagerEmployeeCountDTO("me", 43),
        )
        assertEquals(43, CountActiveEmployees.countFor("me", counts))
    }

    @Test
    fun returnsZeroWhenManagerHasNoActiveEmployees() {
        val counts = listOf(ManagerEmployeeCountDTO("other", 9))
        assertEquals(0, CountActiveEmployees.countFor("me", counts))
    }
}
