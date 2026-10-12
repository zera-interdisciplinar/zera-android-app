package com.zera.android.model.usecase.inventory

import com.zera.android.model.dto.inventory.PagedModelsDTO
import com.zera.android.model.remote.client.InventoryClient

class GetModels(
    private val fetch: suspend (String?, Int, Int) -> PagedModelsDTO = { q, page, size ->
        InventoryClient.inventoryService.getModels(q = q, page = page, size = size)
    },
) {
    suspend fun execute(
        q: String? = null,
        page: Int = 0,
        size: Int = 20,
    ): PagedModelsDTO {
        return fetch(q, page, size)
    }
}
