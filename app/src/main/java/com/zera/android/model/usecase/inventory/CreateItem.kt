package com.zera.android.model.usecase.inventory

import com.zera.android.model.dto.inventory.CreateItemRequestDTO
import com.zera.android.model.dto.inventory.ItemResponseDTO
import com.zera.android.model.remote.client.InventoryClient

class CreateItem(
    private val fetch: suspend (CreateItemRequestDTO) -> ItemResponseDTO = {
        InventoryClient.inventoryService.createItem(it)
    },
) {
    suspend fun execute(request: CreateItemRequestDTO): ItemResponseDTO {
        return fetch(request)
    }
}
