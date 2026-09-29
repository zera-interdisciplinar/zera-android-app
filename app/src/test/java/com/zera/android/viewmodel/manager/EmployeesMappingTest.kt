package com.zera.android.viewmodel.manager

import com.zera.android.model.entity.team.PendingInvite
import com.zera.android.model.entity.team.TeamEmployee
import com.zera.android.model.entity.user.UserRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

class EmployeesMappingTest {
    @Test
    fun hoursUntilTruncatesWholeHoursAndNeverGoesNegative() {
        val now = LocalDateTime.of(2026, 9, 28, 10, 0)
        assertEquals(23, EmployeesViewModel.hoursUntil("2026-09-29T09:00:00", now))
        assertEquals(0, EmployeesViewModel.hoursUntil("2026-09-27T09:00:00", now))
    }

    @Test
    fun composePutsPendingInvitesBeforeActiveEmployees() {
        val pending = listOf(
            PendingInvite(code = "120443", inviteeName = "Carol", expiresAt = "2026-10-03T09:00:00"),
        )
        val employees = listOf(
            TeamEmployee(userId = "u1", name = "João", role = UserRole.EMPLOYEE, status = "ACTIVE"),
        )
        val items = EmployeesViewModel.composeEmployees(employees, pending)
        assertEquals(2, items.size)
        assertTrue(items[0].isPending)
        assertEquals("invite-120443", items[0].id)
        assertEquals("Carol", items[0].name)
        assertEquals("Operador", items[0].role)
        assertEquals("u1", items[1].id)
        assertEquals("João", items[1].name)
        assertEquals("Operador", items[1].role)
    }

    @Test
    fun toInviteCardUsesInviteeNameAndHours() {
        val now = LocalDateTime.of(2026, 10, 2, 9, 0)
        val card = EmployeesViewModel.toInviteCard(
            PendingInvite("120443", "Operadora Carol the Best", "2026-10-03T09:00:00"),
            now,
        )
        assertEquals("120443", card.code)
        assertEquals("Operadora Carol the Best", card.inviteeName)
        assertEquals(24, card.expiresInHours)
    }
}
