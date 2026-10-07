package com.zera.android.view.screens.manager.recycling

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zera.android.model.entity.places.RecyclingPlace
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.inputs.ZeraSearchInput
import com.zera.android.view.components.maps.CurrentLocationMap
import com.zera.android.view.components.maps.MapPoint
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.CaptionText
import com.zera.android.view.components.texts.SubtitleText
import com.zera.android.view.navigation.Route
import com.zera.android.view.screens.manager.ManagerScaffold
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme
import com.zera.android.viewmodel.manager.NearbyRecyclingPlacesViewModel
import com.zera.android.viewmodel.manager.formatDistanceMeters

@Composable
fun RecyclingScreen(
    viewModel: NearbyRecyclingPlacesViewModel = viewModel(),
) {
    val state by viewModel.state
    Log.i("TEMP_PLACES", "RecyclingScreen places=${state.places.size} loading=${state.isLoading}")

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
            onUserLocation = viewModel::onUserLocation,
            points = state.places.map { place ->
                MapPoint(
                    id = place.placeId,
                    latitude = place.latitude,
                    longitude = place.longitude,
                    title = place.displayName(),
                )
            },
            onPointClick = viewModel::onPlaceSelected,
            topOverlay = {
                ZeraSearchInput(
                    value = state.address,
                    onValueChange = viewModel::onAddressChange,
                    onSearch = { /* TODO: buscar endereço no mapa */ },
                    placeholder = "Digite um endereço",
                )
            },
        )
        state.selectedPlace?.let { place ->
            PlaceDetailsPopup(place = place)
        }
        if (state.isLoading || state.statusMessage != null) {
            Column(modifier = Modifier.padding(horizontal = Spacing.medium, vertical = Spacing.small)) {
                if (state.isLoading) {
                    CaptionText(text = "Buscando recicladoras próximas…", color = MaterialTheme.colorScheme.onPrimary)
                }
                state.statusMessage?.let { message ->
                    CaptionText(text = message, color = MaterialTheme.colorScheme.onPrimary)
                }
                if (state.canRetry) {
                    CaptionText(
                        text = "Tentar de novo",
                        color = MaterialTheme.colorScheme.onPrimary,
                        onClick = viewModel::onRetry,
                    )
                }
            }
        }
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
private fun PlaceDetailsPopup(place: RecyclingPlace) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.medium, vertical = Spacing.small),
        shape = RoundedCornerShape(Radius.medium),
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        Column(modifier = Modifier.padding(Spacing.medium)) {
            SubtitleText(text = place.displayName())
            BodyText(text = place.address)
            CaptionText(text = formatDistanceMeters(place.distanceMeters))
        }
    }
}

private fun RecyclingPlace.displayName(): String =
    name.ifBlank { "Recicladora" }

@Composable
@Preview(heightDp = 900)
private fun RecyclingScreenPreview() {
    ZeraTheme {
        RecyclingScreen()
    }
}
