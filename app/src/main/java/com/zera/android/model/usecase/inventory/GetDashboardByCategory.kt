package com.zera.android.model.usecase.inventory

import com.zera.android.model.dto.inventory.DashboardByCategoryResponseDTO
import com.zera.android.model.remote.client.InventoryClient

class GetDashboardByCategory {
    suspend fun execute(): DashboardByCategoryResponseDTO {
        return InventoryClient.inventoryService.getDashboardByCategory()
    }
}
