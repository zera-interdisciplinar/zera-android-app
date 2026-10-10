package com.zera.android.model.usecase.inventory

import com.zera.android.model.remote.client.InventoryClient

class CancelDisposalSchedule {
    suspend fun execute(id: String) {
        InventoryClient.inventoryService.deleteDisposalSchedule(id.trim())
    }
}
