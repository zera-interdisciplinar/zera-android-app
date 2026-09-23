package com.zera.android.viewmodel.navigation

import androidx.lifecycle.ViewModel
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator

class ManagerBottomNavBarViewModel : ViewModel() {
    // TODO: revisar se a troca de aba deveria usar pushAndPop (substituir o topo da
    // pilha) em vez de push, para não empilhar as telas de aba umas sobre as outras.
    fun onHomeClick() {
        ZeraNavigator.push(Route.ManagerHome)
    }

    fun onIndexesClick() {
        ZeraNavigator.push(Route.Indexes)
    }

    fun onEmployeesClick() {
        ZeraNavigator.push(Route.Employees)
    }

    fun onRecyclingClick() {
        // TODO: ainda não existe uma tela de Reciclagem
    }

    fun onItensClick() {
        ZeraNavigator.push(Route.Itens)
    }
}
