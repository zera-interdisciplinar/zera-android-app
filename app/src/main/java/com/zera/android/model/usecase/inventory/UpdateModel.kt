package com.zera.android.model.usecase.inventory

import com.zera.android.model.dto.inventory.ModelResponseDTO
import com.zera.android.model.dto.inventory.UpdateModelManufacturerRequestDTO
import com.zera.android.model.dto.inventory.UpdateModelMaterialsRequestDTO
import com.zera.android.model.dto.inventory.UpdateModelNameRequestDTO
import com.zera.android.model.remote.client.InventoryClient

class UpdateModel {
    suspend fun rename(modelId: String, name: String): ModelResponseDTO =
        InventoryClient.inventoryService.updateModelName(
            modelId,
            UpdateModelNameRequestDTO(name = name),
        )

    suspend fun manufacturer(modelId: String, manufacturer: String): ModelResponseDTO =
        InventoryClient.inventoryService.updateModelManufacturer(
            modelId,
            UpdateModelManufacturerRequestDTO(manufacturer = manufacturer),
        )

    suspend fun materials(modelId: String, materialCodes: List<String>): ModelResponseDTO =
        InventoryClient.inventoryService.updateModelMaterials(
            modelId,
            UpdateModelMaterialsRequestDTO(materials = materialCodes),
        )
}
