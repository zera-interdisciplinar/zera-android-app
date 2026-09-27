package com.zera.android.model.usecase.inventory

import com.zera.android.model.dto.inventory.PagedItemsDTO
import com.zera.android.model.remote.client.InventoryClient

class GetItems {
    suspend fun execute(
        status: String? = null,
        categoryId: String? = null,
        q: String? = null,
        page: Int = 0,
        size: Int = 20,
    ): PagedItemsDTO {
        return InventoryClient.inventoryService.getItems(
            status = status,
            categoryId = categoryId,
            q = q,
            page = page,
            size = size,
        )
    }
}
