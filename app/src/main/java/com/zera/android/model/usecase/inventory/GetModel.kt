package com.zera.android.model.usecase.inventory

import com.zera.android.model.dto.inventory.ModelResponseDTO
import com.zera.android.model.remote.client.InventoryClient

class GetModel(
    private val fetch: suspend (String) -> ModelResponseDTO = {
        InventoryClient.inventoryService.getModel(it)
    },
) {
    suspend fun execute(modelId: String): ModelResponseDTO = fetch(modelId)
}
