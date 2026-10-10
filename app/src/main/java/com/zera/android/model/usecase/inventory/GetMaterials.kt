package com.zera.android.model.usecase.inventory

import com.zera.android.model.dto.inventory.MaterialCatalogDTO
import com.zera.android.model.remote.client.InventoryClient

class GetMaterials {
    suspend fun execute(): List<MaterialCatalogDTO> {
        return InventoryClient.inventoryService.getMaterials()
    }
}
