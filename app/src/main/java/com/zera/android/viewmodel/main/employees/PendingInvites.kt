package com.zera.android.viewmodel.main.employees

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.zera.android.model.usecase.team.ListPendingInvitations
import com.zera.android.view.components.lists.InviteItem
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.viewmodel.ZeraViewModel
import com.zera.android.viewmodel.errorDescription
import com.zera.android.viewmodel.loadErrorDescription
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

data class PendingInvitesState(
    val invites: List<InviteItem> = emptyList(),
    val isLoading: Boolean = true,
    /** A carga falhou: a lista vazia não mostra "Nenhum convite pendente". */
    val loadFailed: Boolean = false,
    val errorMessage: String? = null,
)

class PendingInvitesViewModel : ZeraViewModel() {
    private val listPendingInvitations = ListPendingInvitations()

    private val _state = mutableStateOf(PendingInvitesState())
    val state = _state

    init {
        loadInvites()
    }

    private fun loadInvites() {
        viewModelScope.launch {
            try {
                val invites = listPendingInvitations.execute()
                _state.value = _state.value.copy(
                    invites = invites.map { EmployeesViewModel.toInviteCard(it) },
                    isLoading = false,
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    loadFailed = true,
                    errorMessage = errorDescription(e, loadErrorDescription("os convites")),
                )
            }
        }
    }

    fun onBackClick() {
        ZeraNavigator.goBack()
    }

    fun onErrorDismiss() {
        _state.value = _state.value.copy(errorMessage = null)
    }
}
