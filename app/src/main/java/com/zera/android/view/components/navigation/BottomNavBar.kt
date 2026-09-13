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
import com.zera.android.view.components.buttons.IconButton
import com.zera.android.view.components.texts.LabelText
import com.zera.android.view.navigation.Route
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon

@Composable
fun BottomNavBar(
    modifier: Modifier = Modifier,
    currentRoute: Route
) {
    Row(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerLowest,
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
            route = Route.Welcome,
            icon = ZeraIcon.Home,
            selected = if (currentRoute == Route.ManagerHome || currentRoute == Route.EmployeeHome) true else false,
        )
        ShortCutButton(
            modifier = Modifier.weight(1f),
            label = "Indicadores",
            contentDescription = "Botão para Indicadores",
            route = Route.Welcome,
            icon = ZeraIcon.Graph,
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
            route = Route.Welcome,
            icon = ZeraIcon.Recycle,
            selected = false,
        )
        ShortCutButton(
            modifier = Modifier.weight(1f),
            label = "Itens",
            contentDescription = "Botão para Itens",
            route = Route.Welcome,
            icon = ZeraIcon.Bell,
            selected = false,
        )
    }
}

@Composable
fun ShortCutButton(
    label: String,
    route: Route,
    contentDescription: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    icon: ZeraIcon = ZeraIcon.Placeholder,
) {
    val contentColor =
        if (selected) MaterialTheme.colorScheme.onSurface
        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)

    Button(
        onClick = {
            //TODO: Criar o ViewModel proprio para este componente (Para realizar as navegações)
        },
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
fun BottomNavBarPreview() {
    ZeraTheme {
        BottomNavBar(currentRoute = Route.Welcome)
    }
}
