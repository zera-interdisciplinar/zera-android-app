package com.zera.android.view.components.navigation

import com.zera.android.view.navigation.Route
import com.zera.android.view.theme.icons.ZeraIcon

/**
 * Atalhos da [SideBar] do gestor. Os atalhos fixos (Central de Ajuda, Fale com o Zé e
 * Configurações) não entram aqui: a própria [SideBar] já os desenha.
 */
val ManagerSideBarItems = listOf(
    SideBarItem(label = "Perfil", icon = ZeraIcon.Profile, route = Route.Profile),
    SideBarItem(label = "Colaboradores", icon = ZeraIcon.Group, route = Route.Employees),
    SideBarItem(label = "Alertas", icon = ZeraIcon.Bell, route = Route.EmployeeHome),
    SideBarItem(label = "Modelos", icon = ZeraIcon.Crate, route = Route.Models),
    SideBarItem(label = "Itens", icon = ZeraIcon.Box, route = Route.Itens),
    SideBarItem(label = "Itens em Manutenção", icon = ZeraIcon.Wrench, route = Route.Itens),
)

/**
 * Atalhos da [SideBar] do operário. Rotas `null` são de telas que ainda não existem no
 * fluxo do operário (o botão aparece, mas só fecha a sidebar).
 */
val EmployeeSideBarItems = listOf(
    SideBarItem(label = "Perfil", icon = ZeraIcon.Profile, route = Route.Profile),
    SideBarItem(label = "Alertas", icon = ZeraIcon.Bell), // TODO: rota de Alertas do operário
    SideBarItem(label = "Central de Trabalho", icon = ZeraIcon.BriefCase), // TODO: rota da Central
    SideBarItem(label = "Modelos", icon = ZeraIcon.Crate), // TODO: rota de Modelos do operário
    SideBarItem(label = "Itens", icon = ZeraIcon.Box, route = Route.EmployeeItems),
    SideBarItem(label = "Itens em Manutenção", icon = ZeraIcon.Wrench), // TODO: rota de manutenção
    SideBarItem(label = "Descartes Agendados", icon = ZeraIcon.Recycle), // TODO: rota de descartes
)
