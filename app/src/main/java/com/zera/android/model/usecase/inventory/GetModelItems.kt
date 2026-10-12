package com.zera.android.model.usecase.inventory

import com.zera.android.model.dto.inventory.PagedItemsDTO
import com.zera.android.model.remote.client.InventoryClient
import kotlinx.coroutines.CancellationException

class GetModelItems {
    suspend fun execute(modelId: String): PagedItemsDTO {
        val fromModelPath = try {
            InventoryClient.inventoryService.getModelItems(modelId)
        } catch (error: CancellationException) {
            throw error
        } catch (_: Exception) {
            null
        }
        if (fromModelPath != null && fromModelPath.content.isNotEmpty()) {
            return fromModelPath
        }
        return itemsByModel(modelId)
    }

    private suspend fun itemsByModel(modelId: String): PagedItemsDTO {
        val pending = InventoryClient.inventoryService.getItems(
            status = "PENDING_APPROVAL",
            modelId = modelId,
            page = 0,
            size = 20,
        )
        if (pending.content.isNotEmpty()) return pending
        return InventoryClient.inventoryService.getItems(
            modelId = modelId,
            page = 0,
            size = 20,
        )
    }
}
