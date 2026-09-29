package com.zera.android.viewmodel.manager

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.zera.android.model.entity.team.PendingInvite
import com.zera.android.model.entity.team.TeamEmployee
import com.zera.android.model.entity.user.UserRole
import com.zera.android.model.usecase.team.CountActiveEmployees
import com.zera.android.model.usecase.team.CreateInvitation
import com.zera.android.model.usecase.team.ListPendingInvitations
import com.zera.android.model.usecase.team.ListTeamEmployees
import com.zera.android.view.components.lists.EmployeeItem
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.viewmodel.ZeraViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import retrofit2.HttpException
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
    val showCreateInvite: Boolean = false,
    val inviteeName: String = "",
    val isCreatingInvite: Boolean = false,
    val inviteErrorMessage: String? = null,
    val errorMessage: String? = null,
)

class EmployeesViewModel : ZeraViewModel() {
    private val countActiveEmployees = CountActiveEmployees()
    private val listTeamEmployees = ListTeamEmployees()
    private val listPendingInvitations = ListPendingInvitations()
    private val createInvitation = CreateInvitation()

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
        viewModelScope.launch { loadPendingInvites() }
    }

    private suspend fun loadCount() {
        try {
            val count = countActiveEmployees.execute()
            _state.value = _state.value.copy(activeEmployeesCount = count)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            _state.value = _state.value.copy(errorMessage = e.message ?: "Não foi possível carregar a equipe.")
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
        _state.value = _state.value.copy(
            showCreateInvite = true,
            inviteErrorMessage = null,
        )
    }

    fun onInviteeNameChange(name: String) {
        _state.value = _state.value.copy(inviteeName = name, inviteErrorMessage = null)
    }

    fun onCancelCreateInvite() {
        _state.value = _state.value.copy(
            showCreateInvite = false,
            inviteeName = "",
            isCreatingInvite = false,
            inviteErrorMessage = null,
        )
    }

    fun onSubmitCreateInvite() {
        val name = _state.value.inviteeName.trim()
        if (name.isBlank()) {
            _state.value = _state.value.copy(inviteErrorMessage = "Informe o nome do convidado.")
            return
        }
        _state.value = _state.value.copy(isCreatingInvite = true, inviteErrorMessage = null)
        viewModelScope.launch {
            try {
                createInvitation.execute(name)
                _state.value = _state.value.copy(
                    showCreateInvite = false,
                    inviteeName = "",
                    isCreatingInvite = false,
                    inviteErrorMessage = null,
                )
                loadPendingInvites()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isCreatingInvite = false,
                    inviteErrorMessage = createInviteError(e),
                )
            }
        }
    }

    fun onEmployeeClick(employee: EmployeeItem) {
        // Detalhe do colaborador ainda não tem rota no app.
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

        internal fun createInviteError(error: Exception): String {
            if (error is HttpException) {
                return when (error.code()) {
                    400 -> "Nome inválido ou gestor não reconhecido."
                    404 -> "Gestor não encontrado."
                    401, 403 -> "Sem permissão para criar convite."
                    else -> "Não foi possível criar o convite. Tente novamente."
                }
            }
            return error.message?.takeIf { it.isNotBlank() }
                ?: "Não foi possível criar o convite. Tente novamente."
        }
    }
}
