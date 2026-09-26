package com.zera.android.viewmodel.manager

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.zera.android.model.dto.inventory.ItemResponseDTO
import com.zera.android.model.usecase.inventory.GetItemDetails
import com.zera.android.view.components.outros.ItemStatus
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.viewmodel.ZeraViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.util.Locale

data class ItemDetailsState(
    val itemId: String = "",
    val itemName: String = "",
    val itemSubtitle: String = "",
    val status: ItemStatus? = null,
    val category: String = "",
    val material: String = "",
    val condition: String = "",
    val registeredBy: String = "",
    val registeredAt: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

class ItemDetailsViewModel : ZeraViewModel() {
    private val getItemDetails = GetItemDetails()

    private val _state = mutableStateOf(ItemDetailsState())
    val state = _state

    fun loadItem(itemId: String) {
        if (itemId.isBlank()) return
        _state.value = _state.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            try {
                val item = getItemDetails.execute(itemId)
                _state.value = stateFrom(item).copy(isLoading = false, errorMessage = null)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }

    fun onBackClick() {
        ZeraNavigator.goBack()
    }

    fun onEditClick() {
        // TODO: fluxo de edição do item ainda não definido (regra de negócio pendente)
    }

    fun onApproveClick() {
        // TODO: caso de uso de aprovação do item ainda não definido (regra de negócio pendente)
    }

    companion object {
        private val ptBr = Locale.forLanguageTag("pt-BR")

        internal fun statusFrom(value: String?): ItemStatus? = ItemStatus.fromBackend(value)

        internal fun conditionLabel(value: String?): String = when (value) {
            "NEW" -> "Novo"
            "USED" -> "Usado"
            "SEMI_DAMAGED" -> "Semidanificado"
            "DAMAGED" -> "Danificado"
            else -> value.orEmpty()
        }

        internal fun materialsLabel(names: List<String>): String = when {
            names.isEmpty() -> ""
            names.size == 1 -> names.first()
            names.size == 2 -> "${names[0]} e ${names[1]}"
            else -> names.dropLast(1).joinToString(", ") + " e " + names.last()
        }

        internal fun formatRegisteredAt(iso: String?): String {
            if (iso.isNullOrBlank()) return ""
            return try {
                val dateTime = LocalDateTime.parse(iso)
                val month = dateTime.month
                    .getDisplayName(TextStyle.SHORT, ptBr)
                    .replace(".", "")
                    .replaceFirstChar { it.uppercase() }
                    .take(3)
                val minute = dateTime.minute.toString().padStart(2, '0')
                "${dateTime.dayOfMonth} de $month ${dateTime.year} - ${dateTime.hour}h$minute"
            } catch (_: Exception) {
                iso
            }
        }

        internal fun stateFrom(item: ItemResponseDTO): ItemDetailsState = ItemDetailsState(
            itemId = item.id,
            itemName = item.name,
            itemSubtitle = item.model?.name.orEmpty(),
            status = statusFrom(item.status),
            category = item.model?.category?.name.orEmpty(),
            material = materialsLabel(item.model?.materials.orEmpty().map { it.name }),
            condition = conditionLabel(item.condition),
            registeredBy = item.createdByName.orEmpty(),
            registeredAt = formatRegisteredAt(item.createdAt),
        )
    }
}
