package com.zera.android.viewmodel

import com.zera.android.model.entity.user.UserRole
import com.zera.android.view.navigation.Route

fun homeRouteForRole(role: String): Route? = when (role) {
    UserRole.EMPLOYEE -> Route.EmployeeHome
    UserRole.MANAGER -> Route.ManagerHome
    else -> null
}

fun isEmployeeRole(role: String): Boolean = role == UserRole.EMPLOYEE

fun homeRouteForRoleOrManager(role: String): Route =
    homeRouteForRole(role) ?: Route.ManagerHome

fun canCancelDisposal(role: String): Boolean = role == UserRole.MANAGER
