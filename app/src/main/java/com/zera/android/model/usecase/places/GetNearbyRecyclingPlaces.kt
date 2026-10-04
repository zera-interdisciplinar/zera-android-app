package com.zera.android.model.usecase.places

import com.zera.android.model.dto.places.ProblemDetailDTO
import com.zera.android.model.dto.places.RecyclingPlaceResponseDTO
import com.zera.android.model.entity.places.RecyclingPlace
import com.zera.android.model.remote.client.ApiClient
import kotlinx.serialization.json.Json
import retrofit2.HttpException

class NearbyRecyclingPlacesException(
    val httpCode: Int,
    message: String,
) : Exception(message)

class GetNearbyRecyclingPlaces {
    suspend fun execute(
        lat: Double,
        lng: Double,
        radiusMeters: Int? = null,
    ): List<RecyclingPlace> {
        try {
            return ApiClient.recyclingPlacesService
                .getNearby(lat = lat, lng = lng, radiusMeters = radiusMeters)
                .map { it.toEntity() }
        } catch (error: HttpException) {
            throw NearbyRecyclingPlacesException(
                httpCode = error.code(),
                message = problemDetailMessage(error.response()?.errorBody()?.string())
                    ?: error.message(),
            )
        }
    }
}

private fun RecyclingPlaceResponseDTO.toEntity() = RecyclingPlace(
    placeId = placeId,
    name = name,
    address = address,
    latitude = lat,
    longitude = lng,
    distanceMeters = distanceMeters,
)

private val problemJson = Json { ignoreUnknownKeys = true }

internal fun problemDetailMessage(body: String?): String? {
    if (body.isNullOrBlank()) return null
    return runCatching { problemJson.decodeFromString<ProblemDetailDTO>(body).detail }
        .getOrNull()
        ?.takeIf { it.isNotBlank() }
}
