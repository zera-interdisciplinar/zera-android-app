package com.zera.android.viewmodel.main.employees

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.zera.android.model.entity.team.PendingInvite
import com.zera.android.model.entity.team.TeamEmployee
import com.zera.android.model.entity.user.UserRole
import com.zera.android.model.usecase.team.CountActiveEmployees
import com.zera.android.model.usecase.team.ListPendingInvitations
import com.zera.android.model.usecase.team.ListTeamEmployees
import com.zera.android.view.components.lists.EmployeeItem
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.view.transition.ScreenAnimation
import com.zera.android.viewmodel.ZeraViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

data class PendingInviteCard(
    val code: String,
    val inviteeName: String,
    val expiresInHours: Int,
)

data class EmployeesState(
    val activeEmployeesCount: Int = 0,
    val pendingInvites: List<PendingInviteCard> = emptyList(),
    val employees: List<EmployeeItem> = emptyList(),
    val isLoadingCount: Boolean = true,
    val isLoadingEmployees: Boolean = true,
    val isLoadingInvites: Boolean = true,
    val errorMessage: String? = null,
) {
    /**
     * Carga dos blocos que sempre aparecem na tela (e têm skeleton). Os convites pendentes são
     * opcionais e entram sozinhos quando chegam, por isso [isLoadingInvites] fica de fora.
     */
    val isLoading: Boolean
        get() = isLoadingCount || isLoadingEmployees
}

class EmployeesViewModel : ZeraViewModel() {
    private val countActiveEmployees = CountActiveEmployees()
    private val listTeamEmployees = ListTeamEmployees()
    private val listPendingInvitations = ListPendingInvitations()

    private var refreshInvitesOnResume = false
    private var loadedEmployees: List<TeamEmployee> = emptyList()
    private var loadedInvites: List<PendingInvite> = emptyList()

    private val _state = mutableStateOf(EmployeesState())
    val state = _state

    init {
        loadTeam()
    }

    private fun loadTeam() {
        _state.value = _state.value.copy(errorMessage = null)
        viewModelScope.launch { loadCount() }
        viewModelScope.launch { loadEmployees() }
        viewModelScope.launch {
            // A flag só vale para a carga inicial; a recarga após criar um convite é silenciosa.
            try {
                loadPendingInvites()
            } finally {
                _state.value = _state.value.copy(isLoadingInvites = false)
            }
        }
    }

    private suspend fun loadCount() {
        try {
            val count = countActiveEmployees.execute()
            _state.value = _state.value.copy(activeEmployeesCount = count)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            _state.value = _state.value.copy(errorMessage = e.message ?: "Não foi possível carregar a equipe.")
        } finally {
            _state.value = _state.value.copy(isLoadingCount = false)
        }
    }

    private suspend fun loadEmployees() {
        try {
            loadedEmployees = listTeamEmployees.execute()
            publishRoster()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            _state.value = _state.value.copy(errorMessage = e.message ?: "Não foi possível carregar a equipe.")
        } finally {
            _state.value = _state.value.copy(isLoadingEmployees = false)
        }
    }

    private suspend fun loadPendingInvites() {
        try {
            loadedInvites = listPendingInvitations.execute()
            publishRoster()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            _state.value = _state.value.copy(errorMessage = e.message ?: "Não foi possível carregar os convites.")
        }
    }

    private fun publishRoster() {
        _state.value = _state.value.copy(
            pendingInvites = loadedInvites.map { toInviteCard(it) },
            employees = composeEmployees(loadedEmployees, loadedInvites),
        )
    }

    fun onBackClick() {
        ZeraNavigator.goBack()
    }

    fun onInviteClick() {
        refreshInvitesOnResume = true
        ZeraNavigator.push(Route.EmployeeRegister, ScreenAnimation.SlideHorizontal)
    }

    /** Chamado quando a tela volta a ficar visível; só recarrega após ir criar um convite. */
    fun onResume() {
        if (!refreshInvitesOnResume) return
        refreshInvitesOnResume = false
        viewModelScope.launch { loadPendingInvites() }
    }

    fun onEmployeeClick(employee: EmployeeItem) {
        // Convite pendente ainda não tem usuário, então não há detalhe para abrir.
        if (employee.isPending) return
        ZeraNavigator.push(Route.EmployeeDetails(employeeId = employee.id), ScreenAnimation.SlideHorizontal)
    }

    companion object {
        internal const val ROLE_OPERATOR = "Operador"

        internal fun hoursUntil(
            expiresAt: String,
            now: LocalDateTime = LocalDateTime.now(),
        ): Int {
            return try {
                val end = LocalDateTime.parse(expiresAt)
                ChronoUnit.HOURS.between(now, end).toInt().coerceAtLeast(0)
            } catch (_: Exception) {
                0
            }
        }

        internal fun toInviteCard(
            invite: PendingInvite,
            now: LocalDateTime = LocalDateTime.now(),
        ): PendingInviteCard = PendingInviteCard(
            code = invite.code,
            inviteeName = invite.inviteeName,
            expiresInHours = hoursUntil(invite.expiresAt, now),
        )

        internal fun roleLabel(role: String): String = when (role) {
            UserRole.EMPLOYEE -> ROLE_OPERATOR
            UserRole.MANAGER -> "Gestor"
            else -> role
        }

        internal fun composeEmployees(
            employees: List<TeamEmployee>,
            pending: List<PendingInvite>,
        ): List<EmployeeItem> {
            val pendingItems = pending.map { invite ->
                EmployeeItem(
                    id = "invite-${invite.code}",
                    name = invite.inviteeName,
                    role = ROLE_OPERATOR,
                    isPending = true,
                )
            }
            val activeItems = employees.map { employee ->
                EmployeeItem(
                    id = employee.userId,
                    name = employee.name,
                    role = roleLabel(employee.role),
                    isPending = false,
                )
            }
            return pendingItems + activeItems
        }
    }
}
