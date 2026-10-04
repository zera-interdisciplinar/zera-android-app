package com.zera.android.viewmodel.manager

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.zera.android.model.entity.places.RecyclingPlace
import com.zera.android.model.usecase.places.GetNearbyRecyclingPlaces
import com.zera.android.model.usecase.places.NearbyRecyclingPlacesException
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.view.transition.ScreenAnimation
import com.zera.android.viewmodel.ZeraViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.Locale

// TEMP: remover após debug. Teto do contrato (o servidor corta acima de 20000).
private const val TEMP_LOG_TAG = "TEMP_PLACES"
private const val TEMP_RADIUS_METERS = 20_000

data class NearbyRecyclingPlacesState(
    val canUseCurrentLocation: Boolean = false,
    val address: String = "",
    val places: List<RecyclingPlace> = emptyList(),
    val selectedPlaceId: String? = null,
    val isLoading: Boolean = false,
    val statusMessage: String? = null,
    val canRetry: Boolean = false,
) {
    val selectedPlace: RecyclingPlace?
        get() = places.firstOrNull { it.placeId == selectedPlaceId }

    val canSelectItemsForDisposal: Boolean
        get() = selectedPlaceId != null
}

class NearbyRecyclingPlacesViewModel : ZeraViewModel() {
    private val getNearbyRecyclingPlaces = GetNearbyRecyclingPlaces()
    private val _state = mutableStateOf(NearbyRecyclingPlacesState())
    val state = _state

    private var loadJob: Job? = null
    private var lastLatitude: Double? = null
    private var lastLongitude: Double? = null

    fun onLocationAvailabilityChanged(canUseCurrentLocation: Boolean) {
        _state.value = _state.value.copy(canUseCurrentLocation = canUseCurrentLocation)
    }

    fun onAddressChange(value: String) {
        _state.value = _state.value.copy(address = value)
    }

    fun onUserLocation(latitude: Double, longitude: Double) {
        lastLatitude = latitude
        lastLongitude = longitude
        loadNearby()
    }

    fun onRetry() {
        loadNearby()
    }

    fun onPlaceSelected(placeId: String) {
        _state.value = _state.value.copy(selectedPlaceId = placeId, statusMessage = null)
    }

    fun onSelectItemsForDisposalClick() {
        val place = _state.value.selectedPlace ?: return
        ZeraNavigator.push(
            Route.ItensSelection(
                placeId = place.placeId,
                placeName = place.name,
                placeAddress = place.address,
                distanceMeters = place.distanceMeters,
            ),
            ScreenAnimation.SlideHorizontal,
        )
    }

    private fun loadNearby() {
        val latitude = lastLatitude ?: return
        val longitude = lastLongitude ?: return
        loadJob?.cancel()
        Log.i(TEMP_LOG_TAG, "busca lat=$latitude lng=$longitude radiusMeters=$TEMP_RADIUS_METERS")
        _state.value = _state.value.copy(isLoading = true, statusMessage = null, canRetry = false)
        loadJob = viewModelScope.launch {
            try {
                val places = getNearbyRecyclingPlaces.execute(
                    lat = latitude,
                    lng = longitude,
                    radiusMeters = TEMP_RADIUS_METERS,
                )
                Log.i(TEMP_LOG_TAG, "resposta count=${places.size}")
                places.forEach { place ->
                    Log.i(
                        TEMP_LOG_TAG,
                        "ponto id=${place.placeId} lat=${place.latitude} lng=${place.longitude} dist=${place.distanceMeters}",
                    )
                }
                _state.value = applyPlaces(_state.value, places)
            } catch (error: CancellationException) {
                throw error
            } catch (error: NearbyRecyclingPlacesException) {
                Log.e(TEMP_LOG_TAG, "http ${error.httpCode} ${error.message}")
                _state.value = applyFailure(_state.value, error.httpCode, error.message)
            } catch (error: Exception) {
                Log.e(TEMP_LOG_TAG, "falha ${error.message}", error)
                _state.value = _state.value.copy(
                    isLoading = false,
                    places = emptyList(),
                    selectedPlaceId = null,
                    statusMessage = error.message ?: "Nao foi possivel buscar recicladoras proximas.",
                    canRetry = true,
                )
            }
        }
    }
}

internal fun applyPlaces(
    current: NearbyRecyclingPlacesState,
    places: List<RecyclingPlace>,
): NearbyRecyclingPlacesState {
    val stillSelected = places.any { it.placeId == current.selectedPlaceId }
    return current.copy(
        isLoading = false,
        places = places,
        selectedPlaceId = current.selectedPlaceId.takeIf { stillSelected },
        statusMessage = if (places.isEmpty()) "Nenhuma recicladora encontrada por aqui." else null,
        canRetry = false,
    )
}

internal fun applyFailure(
    current: NearbyRecyclingPlacesState,
    httpCode: Int,
    detail: String?,
): NearbyRecyclingPlacesState {
    val unavailable = httpCode == 503
    return current.copy(
        isLoading = false,
        places = emptyList(),
        selectedPlaceId = null,
        statusMessage = when {
            unavailable && !detail.isNullOrBlank() -> detail
            unavailable -> "Nao foi possivel buscar recicladoras proximas no momento."
            !detail.isNullOrBlank() -> detail
            else -> "Nao foi possivel buscar recicladoras proximas."
        },
        canRetry = unavailable || httpCode >= 500,
    )
}

internal fun formatDistanceMeters(distanceMeters: Long): String {
    if (distanceMeters < 1000) return "$distanceMeters m"
    val kilometers = distanceMeters / 1000.0
    return String.format(Locale.forLanguageTag("pt-BR"), "%.1f km", kilometers)
}
