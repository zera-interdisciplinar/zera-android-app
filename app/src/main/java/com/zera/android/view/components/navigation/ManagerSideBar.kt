package com.zera.android.view.components.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.logo.Logo
import com.zera.android.view.components.texts.LabelText
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon
import com.zera.android.viewmodel.navigation.ManagerSideBarViewModel

/**
 * Sidebar de navegação exclusiva do gestor.
 *
 * Funciona como a [ManagerBottomNavBar]: recebe a [currentRoute] da tela em que está e
 * destaca (e desabilita) o botão que levaria para ela. A navegação em si fica no
 * [ManagerSideBarViewModel].
 *
 * @param modifier modificador externo opcional.
 * @param currentRoute rota da tela atual, usada para destacar o botão correspondente.
 * @param viewModel ViewModel com os handlers de clique de cada botão.
 */
@Composable
fun ManagerSideBar(
    modifier: Modifier = Modifier,
    currentRoute: Route,
    onButtonClicked: () -> Unit = {},
    viewModel: ManagerSideBarViewModel = viewModel(),
) {
    Column(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(topStart = Radius.xLarge, bottomStart = Radius.xLarge),
            )
            .padding(horizontal = Spacing.medium, vertical = Spacing.large)
            .fillMaxHeight(),
        verticalArrangement = Arrangement.spacedBy(Spacing.small),
    ) {
        Box(
            Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,

        ){
            Logo(
                modifier = Modifier
                    .padding(start = Spacing.small, bottom = Spacing.medium)
                    .width(120.dp),
                tint = Color.White
            )
        }

        // TODO: terminar de definir todas as telas, para ai conectar aqui
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            SideBarButton(
                label = "Perfil",
                contentDescription = "Botão para Perfil",
                currentRoute = currentRoute,
                onButtonClicked = onButtonClicked,
                icon = ZeraIcon.Profile,
                route = Route.Profile,
            )
            SideBarButton(
                label = "Colaboradores",
                contentDescription = "Botão para Colaboradores",
                currentRoute = currentRoute,
                onButtonClicked = onButtonClicked,
                icon = ZeraIcon.Group,
                route = Route.Employees,
            )
            SideBarButton(
                label = "Alertas",
                contentDescription = "Botão para Alertas",
                currentRoute = currentRoute,
                onButtonClicked = onButtonClicked,
                icon = ZeraIcon.Bell,
                route = Route.EmployeeHome,
            )
            SideBarButton(
                label = "Modelos",
                contentDescription = "Botão para Modelos",
                currentRoute = currentRoute,
                onButtonClicked = onButtonClicked,
                icon = ZeraIcon.Crate,
                route = Route.Models,
            )
            SideBarButton(
                label = "Itens",
                contentDescription = "Botão para Itens",
                currentRoute = currentRoute,
                onButtonClicked = onButtonClicked,
                icon = ZeraIcon.Box,
                route = Route.Itens,
            )
            SideBarButton(
                label = "Itens em Manutenção",
                contentDescription = "Botão para Itens em Manutenção",
                currentRoute = currentRoute,
                onButtonClicked = onButtonClicked,
                icon = ZeraIcon.Wrench,
                route = Route.Itens,
            )
        }
        Column() {
            SideBarButton(
                label = "Central de Ajuda",
                contentDescription = "Botão para Central de Ajuda",
                currentRoute = currentRoute,
                onButtonClicked = onButtonClicked,
                icon = ZeraIcon.InfoCircle,
                route = Route.EmployeeHome,
            )
            SideBarButton(
                label = "Fale com o Zé",
                contentDescription = "Botão para Fale com o Zé",
                currentRoute = currentRoute,
                onButtonClicked = onButtonClicked,
                route = Route.EmployeeHome,
            )
            SideBarButton(
                label = "Configurações",
                contentDescription = "Botão para Configurações",
                currentRoute = currentRoute,
                onButtonClicked = onButtonClicked,
                route = Route.EmployeeHome,
            )
        }
    }
}

/**
 * Botão da [ManagerSideBar]: ícone + rótulo em linha. Quando [selected], vira uma pílula
 * clara com conteúdo escuro e fica desabilitado (a tela atual não precisa de navegação).
 */
@Composable
private fun SideBarButton(
    label: String,
    contentDescription: String,
    route: Route,
    currentRoute: Route,
    onButtonClicked: () -> Unit = {},
    modifier: Modifier = Modifier,
    icon: ZeraIcon = ZeraIcon.Placeholder,
) {
    val selected = currentRoute == route
    val contentColor =
        if (selected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.onPrimary

    Button(
        onClick = {
            if(route!=currentRoute){
                ZeraNavigator.push(route)
            }
            onButtonClicked()
        },
        modifier = modifier.fillMaxWidth(),
        enabled = !selected,
        contentPadding = PaddingValues(horizontal = Spacing.small, vertical = Spacing.small),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = contentColor,
            disabledContainerColor = MaterialTheme.colorScheme.onPrimary,
            disabledContentColor = MaterialTheme.colorScheme.primary,
        ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ZeraIcon(
                icon = icon,
                contentDescription = contentDescription,
                tint = contentColor,
            )
            LabelText(label, color = contentColor, bold = selected, maxLines = 2)
        }
    }
}

@Composable
@Preview(heightDp = 900)
fun ManagerSideBarPreview() {
    ZeraTheme {
        ManagerSideBar(currentRoute = Route.Profile)
    }
}
