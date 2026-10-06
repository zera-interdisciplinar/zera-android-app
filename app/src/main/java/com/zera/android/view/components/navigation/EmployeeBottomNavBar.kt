package com.zera.android.view.components.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zera.android.view.components.buttons.IconButton
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon
import com.zera.android.view.transition.ScreenAnimation

@Composable
fun EmployeeBottomNavBar(
    modifier: Modifier = Modifier,
    currentRoute: Route
) {
    Row(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(Radius.xLarge),
            )
            .padding(horizontal = Spacing.small, vertical = Spacing.small),
        horizontalArrangement = Arrangement.spacedBy(Spacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ShortCutButton(
            modifier = Modifier.weight(1f),
            label = "Início",
            contentDescription = "Botão para Home",
            onClick = { navigateTo(currentRoute, Route.EmployeeHome) },
            icon = ZeraIcon.Home,
            selected = if (currentRoute == Route.ManagerHome || currentRoute == Route.EmployeeHome) true else false,
        )
        ShortCutButton(
            modifier = Modifier.weight(1f),
            label = "Central",
            contentDescription = "Botão para Central",
            onClick = { navigateTo(currentRoute, Route.WorkingCentral) },
            icon = ZeraIcon.BriefCase,
            selected = currentRoute == Route.WorkingCentral,
        )
        IconButton(
            icon = ZeraIcon.QrCode,
            onClick = { navigateTo(currentRoute,Route.Scan) },
            contentDescription = "Escanear item",
            size = 64.dp
        )
        ShortCutButton(
            modifier = Modifier.weight(1f),
            label = "Reciclagem",
            contentDescription = "Botão para Reciclagem",
            onClick = {}, // TODO: fluxo de navegação do Operário ainda não existe
            icon = ZeraIcon.Recycle,
            selected = false,
        )
        ShortCutButton(
            modifier = Modifier.weight(1f),
            label = "Itens",
            contentDescription = "Botão para Itens",
            onClick = { navigateTo(currentRoute, Route.EmployeeItems) },
            icon = ZeraIcon.Crate,
            selected = currentRoute == Route.EmployeeItems,
        )
    }
}

/**
 * Navega para a aba [target] sem empilhar telas de aba umas sobre as outras (mesma regra da
 * `ManagerBottomNavBarViewModel`): a partir da Home empilha normalmente; de outra tela
 * substitui o topo da pilha; e ao voltar para a Home remove a tela atual e a Home que já
 * estava por baixo dela, restando uma só Home na pilha.
 */
private fun navigateTo(currentRoute: Route, target: Route) {
    when {
        currentRoute == target -> Unit
        currentRoute == Route.EmployeeHome ->
            ZeraNavigator.push(target, animation = ScreenAnimation.None)
        target == Route.EmployeeHome ->
            ZeraNavigator.pushAndPop(target, popCount = 2, animation = ScreenAnimation.None)
        else -> ZeraNavigator.pushAndPop(target, animation = ScreenAnimation.None)
    }
}

@Composable
@Preview(widthDp = 800)
fun EmployeeBottomNavBarPreview() {
    ZeraTheme {
        EmployeeBottomNavBar(currentRoute = Route.Welcome)
    }
}
