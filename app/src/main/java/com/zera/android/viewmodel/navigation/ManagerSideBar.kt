package com.zera.android.viewmodel.navigation

import com.zera.android.view.navigation.Route
import com.zera.android.viewmodel.ZeraViewModel

/**
 * ViewModel da [com.zera.android.view.components.navigation.ManagerSideBar].
 *
 * Cada botão da sidebar tem um handler próprio que recebe a rota da tela atual (mesmo
 * contrato do [ManagerBottomNavBarViewModel]). As rotas de destino e a estratégia de
 * navegação ainda não foram definidas, então os handlers estão vazios.
 */
class ManagerSideBarViewModel : ZeraViewModel() {

    fun onProfileClick(currentRoute: Route) {
        // TODO: navegar para a rota de Perfil.
    }

    fun onEmployeesClick(currentRoute: Route) {
        // TODO: navegar para a rota de Colaboradores.
    }

    fun onAlertsClick(currentRoute: Route) {
        // TODO: navegar para a rota de Alertas.
    }

    fun onModelsClick(currentRoute: Route) {
        // TODO: navegar para a rota de Modelos.
    }

    fun onItensClick(currentRoute: Route) {
        // TODO: navegar para a rota de Itens.
    }

    fun onMaintenanceItensClick(currentRoute: Route) {
        // TODO: navegar para a rota de Itens em Manutenção.
    }

    fun onScheduledDisposalsClick(currentRoute: Route) {
        // TODO: navegar para a rota de Descartes Agendados.
    }

    fun onDisposalGuideClick(currentRoute: Route) {
        // TODO: navegar para a rota de Guia de Descarte.
    }

    fun onHelpCenterClick(currentRoute: Route) {
        // TODO: navegar para a rota de Central de Ajuda.
    }

    fun onAskZeClick(currentRoute: Route) {
        // TODO: navegar para a rota de Fale com o Zé.
    }

    fun onSettingsClick(currentRoute: Route) {
        // TODO: navegar para a rota de Configurações.
    }
}
