package com.zera.android.view.components.maps

import android.Manifest
import android.util.Log
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraColorFamily
import com.zera.android.view.theme.ZeraTheme

private const val DEFAULT_ZOOM = 16f

// TEMP: remover após debug.
private const val TEMP_LOG_TAG = "TEMP_PLACES"

// São Paulo, usado como centro enquanto a localização do usuário ainda não chegou.
private val FallbackLatLng = LatLng(-23.5505, -46.6333)

private const val HIDE_POINTS_OF_INTEREST = """
[
  { "featureType": "poi", "stylers": [ { "visibility": "off" } ] },
  { "featureType": "transit", "stylers": [ { "visibility": "off" } ] }
]
"""

data class MapPoint(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val title: String,
)

/**
 * Mapa que centraliza a câmera na localização atual do usuário.
 *
 * O mapa em si aparece sempre. Falta permissão de localização, ou o serviço de
 * localização do aparelho está desligado, um botão "Usar localização atual" some por
 * cima do mapa: ao tocar, pede o que estiver faltando (permissão em runtime, ou a tela
 * de configurações do sistema pra ligar a localização). Com os dois disponíveis, o botão
 * some e o mapa mostra o indicador de "minha localização" do Google Maps.
 *
 * @param onLocationAvailabilityChanged chamado sempre que muda se dá ou não pra usar a
 *   localização atual (permissão concedida E serviço de localização ligado) — pra quem
 *   estiver por fora do componente (ex.: a tela) decidir o que mostrar.
 * @param onUserLocation chamado com a última coordenada conhecida do aparelho.
 * @param topOverlay conteúdo (ex.: barra de pesquisa) sobreposto ao topo do mapa.
 */
@Composable
fun CurrentLocationMap(
    modifier: Modifier = Modifier,
    onLocationAvailabilityChanged: (Boolean) -> Unit = {},
    onUserLocation: (latitude: Double, longitude: Double) -> Unit = { _, _ -> },
    points: List<MapPoint> = emptyList(),
    onPointClick: (String) -> Unit = {},
    topOverlay: @Composable () -> Unit = {},
) {
    val context = LocalContext.current
    var hasLocationPermission by remember { mutableStateOf(context.hasLocationPermission()) }
    var isLocationServiceEnabled by remember { mutableStateOf(context.isLocationServiceEnabled()) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { grants ->
        hasLocationPermission = grants.values.any { granted -> granted }
    }

    // Nem permissão nem o serviço de localização avisam a gente quando mudam por fora
    // do app (usuário concede a permissão ou liga a localização e volta) — reconferimos
    // ao retomar a tela.
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasLocationPermission = context.hasLocationPermission()
                isLocationServiceEnabled = context.isLocationServiceEnabled()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(FallbackLatLng, DEFAULT_ZOOM)
    }
    val canUseCurrentLocation = hasLocationPermission && isLocationServiceEnabled

    LaunchedEffect(canUseCurrentLocation) {
        Log.i(
            TEMP_LOG_TAG,
            "mapa permissao=$hasLocationPermission servico=$isLocationServiceEnabled",
        )
        onLocationAvailabilityChanged(canUseCurrentLocation)
        if (!canUseCurrentLocation) return@LaunchedEffect
        try {
            LocationServices.getFusedLocationProviderClient(context).lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        Log.i(TEMP_LOG_TAG, "gps lat=${location.latitude} lng=${location.longitude}")
                        onUserLocation(location.latitude, location.longitude)
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(
                            LatLng(location.latitude, location.longitude),
                            DEFAULT_ZOOM,
                        )
                    } else {
                        Log.i(TEMP_LOG_TAG, "gps lastLocation nulo")
                    }
                }
                .addOnFailureListener { error ->
                    Log.i(TEMP_LOG_TAG, "gps falhou ${error.message}")
                }
        } catch (_: SecurityException) {
            Log.i(TEMP_LOG_TAG, "gps SecurityException")
            // Permissão pode ter sido revogada entre a checagem e a chamada; mapa segue no fallback.
        }
    }

    Box(modifier = modifier) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = canUseCurrentLocation,
                mapStyleOptions = MapStyleOptions(HIDE_POINTS_OF_INTEREST),
            ),
            uiSettings = MapUiSettings(myLocationButtonEnabled = false),
        ) {
            Log.i(TEMP_LOG_TAG, "plotando markers=${points.size}")
            points.forEach { point ->
                Marker(
                    state = MarkerState(position = LatLng(point.latitude, point.longitude)),
                    title = point.title,
                    onClick = {
                        onPointClick(point.id)
                        false
                    },
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(Spacing.medium),
        ) {
            topOverlay()
        }

        if (!canUseCurrentLocation) {
            ZeraButton(
                text = "Usar localização atual",
                onClick = {
                    if (!hasLocationPermission) {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                            )
                        )
                    } else if (!isLocationServiceEnabled) {
                        context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    }
                },
                fillMaxWidth = true,
                style = ZeraColorFamily.Yellow,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(Spacing.medium),
            )
        }
    }
}

private fun Context.hasLocationPermission(): Boolean =
    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED

private fun Context.isLocationServiceEnabled(): Boolean {
    val locationManager = getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        ?: return false
    return LocationManagerCompat.isLocationEnabled(locationManager)
}

@Composable
@Preview(heightDp = 400)
private fun CurrentLocationMapPreview() {
    ZeraTheme {
        CurrentLocationMap(modifier = Modifier.fillMaxSize())
    }
}
