package com.zera.android.model.usecase.inventory

import com.zera.android.model.dto.inventory.DisposalScheduleDetailDTO
import com.zera.android.model.remote.client.InventoryClient

class GetDisposalSchedule {
    suspend fun execute(id: String): DisposalScheduleDetailDTO {
        return InventoryClient.inventoryService.getDisposalSchedule(id.trim())
    }
}
