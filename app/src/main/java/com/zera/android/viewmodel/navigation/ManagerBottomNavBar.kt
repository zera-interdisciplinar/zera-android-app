package com.zera.android.viewmodel.navigation

import androidx.lifecycle.ViewModel
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator

class ManagerBottomNavBarViewModel : ViewModel() {
    /**
     * A partir da Home, navega por [ZeraNavigator.push] (empilha normalmente).
     *
     * A partir de qualquer outra tela principal, navega por [ZeraNavigator.pushAndPop]
     * (substitui o topo da pilha), evitando empilhar telas de aba umas sobre as outras.
     *
     * Ao voltar para a Home a partir de outra tela principal, remove 2 telas do topo
     * da pilha (a tela atual e a Home que já estava por baixo dela) antes de empilhar
     * a nova Home, restando assim apenas uma tela de Home na pilha.
     */
    private fun navigate(currentRoute: Route, target: Route) {
        when {
            currentRoute == Route.ManagerHome -> ZeraNavigator.push(target)
            target == Route.ManagerHome -> ZeraNavigator.pushAndPop(target, popCount = 2)
            else -> ZeraNavigator.pushAndPop(target)
        }
    }

    fun onHomeClick(currentRoute: Route) {
        navigate(currentRoute, Route.ManagerHome)
    }

    fun onIndexesClick(currentRoute: Route) {
        navigate(currentRoute, Route.Indexes)
    }

    fun onEmployeesClick(currentRoute: Route) {
        navigate(currentRoute, Route.Employees)
    }

    fun onRecyclingClick() {
        // TODO: ainda não existe uma tela de Reciclagem
    }

    fun onItensClick(currentRoute: Route) {
        navigate(currentRoute, Route.Itens)
    }
}
