package com.zera.android.model.usecase.inventory

import com.zera.android.model.dto.inventory.CreateDisposalRequestDTO
import com.zera.android.model.remote.client.InventoryClient

class CreateDisposal {
    suspend fun execute(
        itemIds: List<String>,
        placeId: String,
        placeName: String,
    ): String {
        val ids = itemIds.map { it.trim() }.filter { it.isNotEmpty() }.distinct()
        require(ids.isNotEmpty()) { "Selecione ao menos um item para registrar o descarte." }
        val created = InventoryClient.inventoryService.createDisposal(
            CreateDisposalRequestDTO(
                destination = RECYCLING,
                placeId = placeId.trim().takeIf { it.isNotEmpty() },
                placeName = placeName.trim().takeIf { it.isNotEmpty() },
                itemIds = ids,
            ),
        )
        val disposalId = created.id.trim()
        require(disposalId.isNotEmpty()) { "O inventário não devolveu o identificador do descarte." }
        return disposalId
    }

    companion object {
        internal const val RECYCLING = "RECYCLING"
    }
}
