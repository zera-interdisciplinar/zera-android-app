package com.zera.android.model.usecase.inventory

import com.zera.android.model.dto.inventory.PagedDisposalsDTO
import com.zera.android.model.remote.client.InventoryClient

class ListDisposalSchedules {
    suspend fun execute(
        page: Int = 0,
        size: Int = 20,
        q: String? = null,
        destination: String? = null,
        disposedFrom: String? = null,
        disposedTo: String? = null,
    ): PagedDisposalsDTO {
        return InventoryClient.inventoryService.getDisposals(
            page = page,
            size = size,
            q = q,
            destination = destination,
            disposedFrom = disposedFrom,
            disposedTo = disposedTo,
        )
    }
}
