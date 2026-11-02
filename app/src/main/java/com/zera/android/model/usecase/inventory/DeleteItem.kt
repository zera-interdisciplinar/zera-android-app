package com.zera.android.model.usecase.inventory

import com.zera.android.model.remote.client.InventoryClient

/** Remoção lógica: o item vai para `REMOVED` e some das listagens. */
class DeleteItem {
    suspend fun execute(itemId: String) {
        InventoryClient.inventoryService.deleteItem(itemId)
        DeletedItems.add(itemId)
    }
}
