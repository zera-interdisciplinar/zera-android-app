package com.zera.android.view.screens.manager.recycling

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.inputs.ZeraSearchInput
import com.zera.android.view.components.maps.CurrentLocationMap
import com.zera.android.view.navigation.Route
import com.zera.android.view.screens.manager.ManagerScaffold
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.viewmodel.manager.RecyclingViewModel

@Composable
fun RecyclingScreen(
    viewModel: RecyclingViewModel = viewModel(),
) {
    val state by viewModel.state

    ManagerScaffold(
        title = "Reciclagem",
        currentRoute = Route.Recycling,
        backgroundVariant = true,
        scrollable = false,
        edgeFade = false,
        contentPadding = Spacing.none,
        fabIcon = null,
    ) {
        CurrentLocationMap(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            onLocationAvailabilityChanged = viewModel::onLocationAvailabilityChanged,
            topOverlay = {
                ZeraSearchInput(
                    value = state.address,
                    onValueChange = viewModel::onAddressChange,
                    onSearch = { /* TODO: buscar endereço no mapa */ },
                    placeholder = "Digite um endereço",
                )
            },
        )
        ZeraButton(
            text = "Selecionar itens para descarte",
            onClick = viewModel::onSelectItemsForDisposalClick,
            enabled = state.canSelectItemsForDisposal,
            fillMaxWidth = true,
            style = ZeraColorFamily.Green,
            modifier = Modifier.padding(horizontal = Spacing.medium),
        )
    }
}

@Composable
@Preview(heightDp = 900)
private fun RecyclingScreenPreview() {
    ZeraTheme {
        RecyclingScreen()
    }
}
