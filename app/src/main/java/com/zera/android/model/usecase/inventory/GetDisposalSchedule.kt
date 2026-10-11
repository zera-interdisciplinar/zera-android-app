package com.zera.android.model.usecase.inventory

import com.zera.android.model.dto.inventory.DisposalRecordDTO
import com.zera.android.model.remote.client.InventoryClient

class GetDisposalSchedule {
    suspend fun execute(id: String): DisposalRecordDTO {
        return InventoryClient.inventoryService.getDisposal(id.trim())
    }
}
