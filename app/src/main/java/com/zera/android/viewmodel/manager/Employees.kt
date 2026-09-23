package com.zera.android.viewmodel.manager

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.zera.android.view.components.lists.EmployeeItem
import com.zera.android.view.navigation.ZeraNavigator

data class EmployeesState(
    val activeEmployeesCount: Int = 0,
    val hasPendingInvites: Boolean = false,
    val pendingInviteCode: String = "",
    val pendingInviteOperatorName: String = "",
    val pendingInviteExpiresIn: Int = 0,
    val employees: List<EmployeeItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

class EmployeesViewModel : ViewModel() {
    // TODO: substituir os dados de exemplo pela chamada ao back (ver loadEmployees)
    private val _state = mutableStateOf(
        EmployeesState(
            activeEmployeesCount = 43,
            hasPendingInvites = true,
            pendingInviteCode = "120443",
            pendingInviteOperatorName = "Operadora Carol the Best",
            pendingInviteExpiresIn = 23,
            employees = listOf(
                EmployeeItem(id = "1", name = "Caroll the Best", role = "Operador", isPending = true),
                EmployeeItem(id = "2", name = "Kevin Não Jun", role = "Operador"),
                EmployeeItem(id = "3", name = "Artur Sem H", role = "Operador"),
                EmployeeItem(id = "4", name = "Gustavo Amex", role = "Operador"),
                EmployeeItem(id = "5", name = "Enzo Casares", role = "Operador"),
            ),
        )
    )
    val state = _state

    init {
        loadEmployees()
    }

    private fun loadEmployees() {
        // TODO: buscar colaboradores e convites pendentes no back e atualizar o _state
        // (isLoading / errorMessage inclusos)
    }

    fun onBackClick() {
        ZeraNavigator.goBack()
    }

    fun onInviteClick() {
        // TODO: fluxo de convite de novo colaborador ainda não definido
    }

    fun onCopyInviteCodeClick() {
        // TODO: copiar código do convite para a área de transferência
    }

    fun onEmployeeClick(employee: EmployeeItem) {
        // TODO: navegar para detalhes do colaborador
    }
}
