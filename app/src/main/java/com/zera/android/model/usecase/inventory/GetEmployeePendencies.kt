package com.zera.android.model.usecase.inventory

import com.zera.android.model.dto.inventory.WorkCenterResponseDTO
import com.zera.android.model.remote.client.InventoryClient

class GetEmployeePendencies {
    suspend fun execute(): WorkCenterResponseDTO {
        return InventoryClient.inventoryService.getWorkCenter()
    }
}
