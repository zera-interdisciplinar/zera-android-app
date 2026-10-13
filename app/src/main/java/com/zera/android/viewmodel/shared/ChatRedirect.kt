package com.zera.android.viewmodel.shared

import com.zera.android.model.entity.user.UserRole
import com.zera.android.view.navigation.Route
import com.zera.android.viewmodel.isEmployeeRole

object ChatRedirect {
    // list of screens that are allowed for employee role
    private val employeeScreens = setOf(
        "EmployeeHome",
        "Scan",
        "EmployeeItems",
        "WorkingCentral",
        "Recycling",
        "ScheduledDisposals",
        "Profile",
        "Models",
    )

    // list of screens that are allowed for manager role
    private val managerScreens = setOf(
        "ManagerHome",
        "Employees",
        "Indexes",
        "Itens",
        "Recycling",
        "ScheduledDisposals",
        "Profile",
        "Categories",
        "Models",
        "CategoryCreation",
        "ModelCreation",
    )

    fun resolve(screen: String?, message: String?, role: String): Pair<Route, String>? {
        val name = screen?.trim().orEmpty()
        val label = message?.trim().orEmpty()
        if (name.isEmpty() || label.isEmpty()) return null
        if (!allowed(name, role)) return null
        val route = routeFor(name) ?: return null
        return route to label
    }

    internal fun allowed(screen: String, role: String): Boolean {
        val screens = if (isEmployeeRole(role) || role == UserRole.EMPLOYEE) {
            employeeScreens
        } else {
            managerScreens
        }
        return screen in screens
    }

    internal fun routeFor(screen: String): Route? = when (screen) {
        "EmployeeHome" -> Route.EmployeeHome
        "Scan" -> Route.Scan
        "EmployeeItems" -> Route.EmployeeItems()
        "WorkingCentral" -> Route.WorkingCentral
        "Recycling" -> Route.Recycling
        "ScheduledDisposals" -> Route.ScheduledDisposals
        "Profile" -> Route.Profile
        "Models" -> Route.Models
        "ManagerHome" -> Route.ManagerHome
        "Employees" -> Route.Employees
        "Indexes" -> Route.Indexes
        "Itens" -> Route.Itens()
        "Categories" -> Route.Categories
        "CategoryCreation" -> Route.CategoryCreation
        "ModelCreation" -> Route.ModelCreation
        else -> null
    }
}
