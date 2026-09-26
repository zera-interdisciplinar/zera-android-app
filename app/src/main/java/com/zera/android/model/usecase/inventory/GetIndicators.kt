package com.zera.android.model.usecase.inventory

import com.zera.android.model.dto.inventory.IndicatorsResponseDTO
import com.zera.android.model.remote.client.InventoryClient

class GetIndicators {
    suspend fun execute(from: String? = null, to: String? = null): IndicatorsResponseDTO {
        return InventoryClient.inventoryService.getIndicators(from = from, to = to)
    }
}
