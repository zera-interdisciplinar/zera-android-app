package com.zera.android.viewmodel.manager

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.zera.android.model.usecase.inventory.CreateDisposal
import com.zera.android.model.usecase.recycling.GetRecyclingContact
import com.zera.android.view.components.cards.OpeningHoursItem
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.view.transition.ScreenAnimation
import com.zera.android.viewmodel.ZeraViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.util.Locale

data class RecyclingResumeState(
    val placeId: String = "",
    val recyclerName: String = "",
    val acceptedMaterials: String = "",
    val address: String = "",
    val distance: String = "",
    val isOpen: Boolean? = null,
    val description: String = "",
    val openingHours: List<OpeningHoursItem> = emptyList(),
    val itemIds: List<String> = emptyList(),
    val itemNames: List<String> = emptyList(),
    val contactEmail: String = "",
    val contactPhone: String = "",
    val isLoading: Boolean = false,
    val isConfirming: Boolean = false,
    val errorMessage: String? = null,
) {
    /** Endereço e distância na mesma linha (ex.: "Av. Pavão, 620 · 2,8 km"); omite a parte vazia. */
    val addressLabel: String
        get() = listOf(address, distance).filter { it.isNotEmpty() }.joinToString(" · ")
}

class RecyclingResumeViewModel : ZeraViewModel() {
    private val getRecyclingContact = GetRecyclingContact()
    private val createDisposal = CreateDisposal()
    private val _state = mutableStateOf(RecyclingResumeState())
    val state = _state

    private var boundKey: String? = null

    fun bind(
        placeId: String,
        placeName: String,
        placeAddress: String,
        distanceMeters: Long,
        itemIds: List<String>,
        itemNames: List<String>,
        isOpen: Boolean? = null,
        description: String = "",
        openingDays: List<String> = emptyList(),
        openingHourLabels: List<String> = emptyList(),
        recyclingBusinessId: String = "",
        contactEmail: String = "",
    ) {
        val key = "$placeId|${itemIds.joinToString()}"
        if (boundKey == key) return
        boundKey = key
        val hours = openingHours(openingDays, openingHourLabels)
        val hasContact = recyclingBusinessId.isNotBlank()
        _state.value = RecyclingResumeState(
            placeId = placeId,
            recyclerName = placeName.ifBlank { "Recicladora" },
            acceptedMaterials = materialsSummary(itemNames),
            address = placeAddress,
            distance = formatDistanceMeters(distanceMeters),
            isOpen = isOpen,
            description = description,
            openingHours = hours,
            itemIds = itemIds,
            itemNames = itemNames,
            isLoading = hasContact,
        )
        if (hasContact) loadContact(recyclingBusinessId, contactEmail)
    }

    fun onConfirmClick() {
        val current = _state.value
        if (current.itemIds.isEmpty() || current.isConfirming) return
        _state.value = current.copy(isConfirming = true, errorMessage = null)
        viewModelScope.launch {
            try {
                val disposalId = createDisposal.execute(
                    itemIds = current.itemIds,
                    placeId = current.placeId,
                    placeName = current.recyclerName,
                )
                _state.value = _state.value.copy(isConfirming = false)
                ZeraNavigator.pushAndPop(
                    Route.SchedulingSuccess(
                        recyclerName = current.recyclerName,
                        scheduledAt = formatScheduledAt(LocalDateTime.now()),
                        materials = current.acceptedMaterials,
                        contactEmail = current.contactEmail,
                        contactPhone = current.contactPhone,
                        itemNames = current.itemNames,
                        disposalId = disposalId,
                    ),
                    animation = ScreenAnimation.SlideHorizontal,
                )
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _state.value = _state.value.copy(
                    isConfirming = false,
                    errorMessage = confirmError(error),
                )
            }
        }
    }

    fun onViewItemsClick() {
        val current = _state.value
        ZeraNavigator.push(
            Route.ItensResume(
                itemIds = current.itemIds,
                itemNames = current.itemNames,
            ),
            animation = ScreenAnimation.SlideHorizontal,
        )
    }

    private fun loadContact(recyclingBusinessId: String, email: String) {
        viewModelScope.launch {
            try {
                val contact = getRecyclingContact.execute(recyclingBusinessId, email)
                _state.value = _state.value.copy(
                    contactEmail = contact.email,
                    contactPhone = contact.phone,
                    isLoading = false,
                )
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                val current = _state.value
                _state.value = current.copy(
                    isLoading = false,
                    errorMessage = if (current.isConfirming) current.errorMessage else error.message,
                )
            }
        }
    }

    companion object {
        internal fun openingHours(days: List<String>, hours: List<String>): List<OpeningHoursItem> =
            days.zip(hours)
                .map { (day, hour) -> OpeningHoursItem(days = day, hours = hour) }
                .filter { it.days.isNotBlank() || it.hours.isNotBlank() }

        internal fun materialsSummary(names: List<String>): String {
            if (names.isEmpty()) return "Itens selecionados"
            if (names.size <= 3) return names.joinToString(", ")
            return names.take(2).joinToString(", ") + " e mais ${names.size - 2}"
        }

        internal fun formatScheduledAt(now: LocalDateTime): String {
            val month = now.month.getDisplayName(TextStyle.FULL, Locale.forLanguageTag("pt-BR"))
            return "${now.dayOfMonth} de $month · %02d:%02d".format(now.hour, now.minute)
        }

        internal fun confirmError(error: Exception): String {
            if (error is HttpException) {
                return when (error.code()) {
                    400 -> "Não foi possível registrar o descarte. Confira os itens e o destino."
                    403 -> "Você não tem permissão para registrar o descarte."
                    404 -> "Item não encontrado para este descarte."
                    409 -> "Um ou mais itens não podem ser descartados agora."
                    else -> error.message?.takeIf { it.isNotBlank() }
                        ?: "Não foi possível registrar o descarte."
                }
            }
            return error.message?.takeIf { it.isNotBlank() }
                ?: "Não foi possível registrar o descarte."
        }
    }
}
