package com.zera.android.view.components.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.buttons.IconButton
import com.zera.android.view.components.texts.LabelText
import com.zera.android.view.navigation.Route
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon
import com.zera.android.viewmodel.navigation.ManagerBottomNavBarViewModel

@Composable
fun ManagerBottomNavBar(
    modifier: Modifier = Modifier,
    currentRoute: Route,
    viewModel: ManagerBottomNavBarViewModel = viewModel(),
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
            onClick = viewModel::onHomeClick,
            icon = ZeraIcon.Home,
            selected = currentRoute == Route.ManagerHome,
        )
        ShortCutButton(
            modifier = Modifier.weight(1f),
            label = "Indicadores",
            contentDescription = "Botão para Indicadores",
            onClick = viewModel::onIndexesClick,
            icon = ZeraIcon.Graph,
            selected = currentRoute == Route.Indexes,
        )
        IconButton(
            icon = ZeraIcon.Plus,
            onClick = viewModel::onEmployeesClick,
            contentDescription = "Colaboradores",
            enabled = currentRoute != Route.Employees,
            size = 64.dp
        )
        ShortCutButton(
            modifier = Modifier.weight(1f),
            label = "Reciclagem",
            contentDescription = "Botão para Reciclagem",
            onClick = viewModel::onRecyclingClick,
            icon = ZeraIcon.Recycle,
            selected = false, // TODO: ainda não existe uma Route de Reciclagem para comparar
        )
        ShortCutButton(
            modifier = Modifier.weight(1f),
            label = "Itens",
            contentDescription = "Botão para Itens",
            onClick = viewModel::onItensClick,
            icon = ZeraIcon.Bell,
            selected = currentRoute == Route.Itens,
        )
    }
}

@Composable
fun ShortCutButton(
    label: String,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    icon: ZeraIcon = ZeraIcon.Placeholder,
) {
    val contentColor =
        if (selected) MaterialTheme.colorScheme.onSurface
        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)

    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = !selected,
        contentPadding = PaddingValues(Spacing.micro),
        colors = ButtonDefaults.textButtonColors(
            contentColor = contentColor,
            disabledContentColor = MaterialTheme.colorScheme.onSurface,
        ),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            ZeraIcon(
                icon = icon,
                contentDescription = contentDescription,
            )
            LabelText(label, color = contentColor, bold = selected)
        }
    }
}


@Composable
@Preview(widthDp = 800)
fun ManagerBottomNavBarPreview() {
    ZeraTheme {
        ManagerBottomNavBar(currentRoute = Route.ManagerHome)
    }
}
