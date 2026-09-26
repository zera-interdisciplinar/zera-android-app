package com.zera.android.model.usecase.inventory

import com.zera.android.model.dto.inventory.DashboardHomeResponseDTO
import com.zera.android.model.remote.client.InventoryClient

class GetManagerHome {
    suspend fun execute(): DashboardHomeResponseDTO {
        return InventoryClient.inventoryService.getHome()
    }
}
