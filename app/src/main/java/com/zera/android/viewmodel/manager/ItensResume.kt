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

    fun onItemClick(item: ProductItem) {
        // TODO: definir a ação ao tocar em um item da lista
    }

    // TODO: Decidir se os dados devem ser passados por API, ou localmente na hora da navegação
}
