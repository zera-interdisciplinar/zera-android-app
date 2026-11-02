package com.zera.android.model.usecase.inventory

import com.zera.android.model.remote.client.InventoryClient

class DeleteModel {
    suspend fun execute(modelId: String) {
        InventoryClient.inventoryService.deleteModel(modelId)
    }
}
