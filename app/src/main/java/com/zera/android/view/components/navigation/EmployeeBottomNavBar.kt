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
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon

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
            onClick = {}, // TODO: fluxo de navegação do Operário ainda não existe
            icon = ZeraIcon.Home,
            selected = if (currentRoute == Route.ManagerHome || currentRoute == Route.EmployeeHome) true else false,
        )
        ShortCutButton(
            modifier = Modifier.weight(1f),
            label = "Central",
            contentDescription = "Botão para Central",
            onClick = {}, // TODO: fluxo de navegação do Operário ainda não existe
            icon = ZeraIcon.BriefCase,
            selected = false,
        )
        IconButton(
            icon = ZeraIcon.QrCode,
            onClick = {},
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
            onClick = {}, // TODO: fluxo de navegação do Operário ainda não existe
            icon = ZeraIcon.Crate,
            selected = false,
        )
    }
}

@Composable
@Preview(widthDp = 800)
fun EmployeeBottomNavBarPreview() {
    ZeraTheme {
        EmployeeBottomNavBar(currentRoute = Route.Welcome)
    }
}
