package com.zera.android.model.usecase.inventory

import com.zera.android.model.remote.client.InventoryClient

class DeleteCategory {
    suspend fun execute(categoryId: String) {
        InventoryClient.inventoryService.deleteCategory(categoryId)
    }
}
