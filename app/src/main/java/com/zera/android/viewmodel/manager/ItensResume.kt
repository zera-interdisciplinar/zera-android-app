package com.zera.android.viewmodel.manager

import androidx.compose.runtime.mutableStateOf
import com.zera.android.view.components.lists.ProductItem
import com.zera.android.viewmodel.ZeraViewModel

data class ItensResumeState(
    val items: List<ProductItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

class ItensResumeViewModel : ZeraViewModel() {
    private val _state = mutableStateOf(ItensResumeState())
    val state = _state

    fun bind(itemIds: List<String>, itemNames: List<String>) {
        _state.value = ItensResumeState(items = zipSelectedItems(itemIds, itemNames))
    }

    companion object {
        internal fun zipSelectedItems(itemIds: List<String>, itemNames: List<String>): List<ProductItem> {
            val count = minOf(itemIds.size, itemNames.size)
            return List(count) { index ->
                ProductItem(id = itemIds[index], name = itemNames[index])
            }
        }
    }
}
