package com.zera.android.view.screens.manager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.heightIn
import androidx.compose.ui.unit.dp
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.cards.InviteCard
import com.zera.android.view.components.containers.ZeraBox
import com.zera.android.view.components.lists.EmployeeList
import com.zera.android.view.components.texts.HeadlineText
import com.zera.android.view.components.texts.LabelText
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.navigation.Route
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.view.theme.icons.ZeraIcon
import com.zera.android.viewmodel.manager.EmployeesViewModel

@Composable
fun EmployeesScreen(
    viewModel: EmployeesViewModel = viewModel(),
) {
    val state by viewModel.state

    ManagerScaffold(
        title = "Colaboradores",
        currentRoute = Route.Employees,
        goBack = true,
        onBackClick = viewModel::onBackClick,
        onFabClick = { /* TODO: abrir chatbot */ },
    ) {
        ZeraBox(style = ZeraColorFamily.Blue) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Spacing.micro),
                ) {
                    LabelText(text = "Equipe ativa", color = LocalContentColor.current)
                    HeadlineText(
                        text = "${state.activeEmployeesCount} pessoas",
                        bold = true,
                        color = LocalContentColor.current,
                    )
                }
                ZeraButton(
                    text = "Novo",
                    onClick = viewModel::onInviteClick,
                    style = ZeraColorFamily.Yellow,
                    icon = ZeraIcon.Plus,
                )
            }
        }

        if (state.hasPendingInvites) {
            TitleText(
                text = "Convites pendentes",
                bold = true,
                color = MaterialTheme.colorScheme.onSurface,
            )
            InviteCard(
                code = state.pendingInviteCode,
                name = state.pendingInviteOperatorName,
                expireTime = state.pendingInviteExpiresIn,
                onCopyCodeClick = viewModel::onCopyInviteCodeClick,
            )
        }

        // TODO: EmployeeList é uma LazyColumn com scroll próprio (limitada por heightIn)
        // dentro da Column rolável do ManagerScaffold — revisar essa dupla rolagem depois.
        EmployeeList(
            employees = state.employees,
            onItemClick = viewModel::onEmployeeClick,
            contentPadding = PaddingValues(Spacing.none),
            modifier = Modifier.heightIn(max = 400.dp),
        )
    }
}

@Composable
@Preview(heightDp = 900)
fun EmployeesScreenPreview() {
    ZeraTheme {
        EmployeesScreen()
    }
}
