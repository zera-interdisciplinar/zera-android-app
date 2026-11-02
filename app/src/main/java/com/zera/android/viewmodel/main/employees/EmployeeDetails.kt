package com.zera.android.viewmodel.main.employees

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.zera.android.model.remote.client.isConnectionError
import com.zera.android.model.dto.user.SelfUserResponseDTO
import com.zera.android.model.usecase.auth.GetSelfUser
import com.zera.android.model.usecase.telephone.GetTelephone
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.viewmodel.ZeraViewModel
import com.zera.android.viewmodel.errorDescription
import com.zera.android.viewmodel.loadErrorDescription
import com.zera.android.viewmodel.profile.ProfileViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class EmployeeDetailsState(
    val employeeId: String = "",
    val name: String = "",
    val initials: String = "",
    val photoUrl: String? = null,
    val email: String = "",
    val phone: String = "",
    val position: String = "",
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)

class EmployeeDetailsViewModel(
    private val getUser: GetSelfUser = GetSelfUser(),
    private val getTelephone: GetTelephone = GetTelephone(),
) : ZeraViewModel() {
    private val _state = mutableStateOf(EmployeeDetailsState())
    val state = _state

    fun loadEmployee(employeeId: String) {
        if (employeeId.isBlank()) {
            // Sem id não há o que carregar; evita deixar o isLoading inicial preso em true.
            _state.value = _state.value.copy(isLoading = false)
            return
        }
        // A tela recompõe ao voltar de outra rota; não refaz a carga do mesmo operário.
        if (employeeId == _state.value.employeeId && !_state.value.isLoading) return
        _state.value = _state.value.copy(employeeId = employeeId, isLoading = true, errorMessage = null)
        viewModelScope.launch {
            val user = try {
                getUser.execute(employeeId)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, errorMessage = loadError(e))
                return@launch
            }
            _state.value = stateFrom(user).copy(isLoading = true)
            // Telefone é rota à parte; se falhar, a tela segue com os dados do usuário.
            val phoneError = try {
                val telephone = getTelephone.execute(employeeId)
                _state.value = _state.value.copy(phone = telephone?.number.orEmpty())
                null
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                errorDescription(e, loadErrorDescription("o telefone"))
            }
            _state.value = _state.value.copy(isLoading = false, errorMessage = phoneError)
        }
    }

    fun onErrorDismiss() {
        _state.value = _state.value.copy(errorMessage = null)
    }

    fun onBackClick() {
        ZeraNavigator.goBack()
    }

    fun onDeleteClick() {
        // TODO: confirmar e excluir o perfil do operário. O adm-core ainda não expõe rota de
        //  exclusão/desativação de usuário.
    }

    companion object {
        internal fun initialsFrom(name: String): String = ProfileViewModel.initialsFrom(name)

        internal fun stateFrom(user: SelfUserResponseDTO): EmployeeDetailsState = EmployeeDetailsState(
            employeeId = user.userId,
            name = user.name,
            initials = initialsFrom(user.name),
            photoUrl = user.imageUrl,
            email = user.email,
            position = EmployeesViewModel.roleLabel(user.role),
            isLoading = false,
        )

        /** Descrição do popup para a falha ao carregar o operário; `null` quando é falta de conexão. */
        internal fun loadError(error: Exception): String? {
            if (error.isConnectionError()) return null
            if (error is HttpException) {
                return when (error.code()) {
                    400, 404 -> "Operário não encontrado."
                    401, 403 -> "Sem permissão para ver este operário."
                    else -> "Não foi possível carregar o operário."
                }
            }
            return "Não foi possível carregar o operário."
        }
    }
}
