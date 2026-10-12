package com.zera.android.viewmodel

import com.zera.android.model.entity.user.UserRole
import com.zera.android.view.navigation.Route
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeRouteTest {
    @Test
    fun employeeHomeAfterDisposal() {
        assertEquals(Route.EmployeeHome, homeRouteForRoleOrManager(UserRole.EMPLOYEE))
    }

    @Test
    fun managerHomeAfterDisposal() {
        assertEquals(Route.ManagerHome, homeRouteForRoleOrManager(UserRole.MANAGER))
    }

    @Test
    fun unknownRoleFallsBackToManagerHome() {
        assertEquals(Route.ManagerHome, homeRouteForRoleOrManager(""))
    }

    @Test
    fun onlyManagerCanCancelDisposal() {
        assertTrue(canCancelDisposal(UserRole.MANAGER))
        assertFalse(canCancelDisposal(UserRole.EMPLOYEE))
        assertFalse(canCancelDisposal(""))
    }

    @Test
    fun isEmployeeRole() {
        assertTrue(isEmployeeRole(UserRole.EMPLOYEE))
        assertFalse(isEmployeeRole(UserRole.MANAGER))
    }
}
