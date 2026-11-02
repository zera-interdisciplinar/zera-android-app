package com.zera.android.viewmodel.main.employees

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.zera.android.model.entity.team.PendingInvite
import com.zera.android.model.entity.team.TeamEmployee
import com.zera.android.model.entity.user.UserRole
import com.zera.android.model.remote.client.isConnectionError
import com.zera.android.model.usecase.team.CountActiveEmployees
import com.zera.android.model.usecase.team.CreateInvitation
import com.zera.android.model.usecase.team.ListPendingInvitations
import com.zera.android.model.usecase.team.ListTeamEmployees
import com.zera.android.view.components.lists.EmployeeItem
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.view.transition.ScreenAnimation
import com.zera.android.viewmodel.ZeraViewModel
import com.zera.android.viewmodel.errorDescription
import com.zera.android.viewmodel.loadErrorDescription
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

data class InviteDraft(
    val name: String = "",
    val nameError: String? = null,
)

data class EmployeesState(
    val activeEmployeesCount: Int = 0,
    val pendingInvites: List<PendingInviteCard> = emptyList(),
    val employees: List<EmployeeItem> = emptyList(),
    val isLoadingCount: Boolean = true,
    val isLoadingEmployees: Boolean = true,
    val isLoadingInvites: Boolean = true,
    /** A carga da lista falhou: a lista vazia não mostra "Nenhum colaborador encontrado". */
    val employeesLoadFailed: Boolean = false,
    /** Rascunho do bottom sheet "Criar convite"; `null` = sheet fechado. */
    val inviteDraft: InviteDraft? = null,
    val isSubmitting: Boolean = false,
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
            showError(errorDescription(e, loadErrorDescription("a equipe")))
        } finally {
            _state.value = _state.value.copy(isLoadingCount = false)
        }
    }

    private suspend fun loadEmployees() {
        try {
            loadedEmployees = listTeamEmployees.execute()
            _state.value = _state.value.copy(employeesLoadFailed = false)
            publishRoster()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            _state.value = _state.value.copy(employeesLoadFailed = true)
            showError(errorDescription(e, loadErrorDescription("os colaboradores")))
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
            showError(errorDescription(e, loadErrorDescription("os convites")))
        }
    }

    /** As três cargas rodam em paralelo; uma falha de conexão (`null`) não apaga o erro de outra. */
    private fun showError(description: String?) {
        if (description != null) _state.value = _state.value.copy(errorMessage = description)
    }

    fun onErrorDismiss() {
        _state.value = _state.value.copy(errorMessage = null)
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
        _state.value = _state.value.copy(inviteDraft = InviteDraft())
    }

    fun onInviteDismiss() {
        if (_state.value.isSubmitting) return
        _state.value = _state.value.copy(inviteDraft = null)
    }

    fun onInviteNameChange(value: String) {
        val draft = _state.value.inviteDraft ?: return
        _state.value = _state.value.copy(inviteDraft = draft.copy(name = value, nameError = null))
    }

    fun onCreateInviteClick() {
        val current = _state.value
        val draft = current.inviteDraft ?: return
        if (current.isSubmitting) return
        val nameError = inviteNameValidationError(draft.name)
        if (nameError != null) {
            _state.value = current.copy(inviteDraft = draft.copy(nameError = nameError))
            return
        }
        _state.value = current.copy(isSubmitting = true, errorMessage = null)
        viewModelScope.launch {
            try {
                createInvitation.execute(draft.name)
                _state.value = _state.value.copy(isSubmitting = false, inviteDraft = null)
                loadPendingInvites()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isSubmitting = false,
                    errorMessage = createInviteError(e),
                )
            }
        }
    }

    fun onEmployeeClick(employee: EmployeeItem) {
        // Convite pendente ainda não tem usuário, então não há detalhe para abrir.
        if (employee.isPending) return
        ZeraNavigator.push(Route.EmployeeDetails(employeeId = employee.id), ScreenAnimation.SlideHorizontal)
    }

    companion object {
        internal const val ROLE_OPERATOR = "Operador"

        /** Descrição do popup para a falha ao criar o convite; `null` quando é falta de conexão. */
        internal fun createInviteError(error: Exception): String? {
            if (error.isConnectionError()) return null
            if (error is HttpException) {
                return when (error.code()) {
                    400 -> "Nome inválido ou gestor não reconhecido."
                    404 -> "Gestor não encontrado."
                    401, 403 -> "Sem permissão para criar convite."
                    else -> "Não foi possível criar o convite. Tente novamente."
                }
            }
            return "Não foi possível criar o convite. Tente novamente."
        }

        internal fun inviteNameValidationError(value: String): String? =
            if (value.isBlank()) "Informe o nome do convite." else null

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
