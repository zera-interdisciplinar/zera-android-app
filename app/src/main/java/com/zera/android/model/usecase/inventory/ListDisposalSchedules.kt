package com.zera.android.model.usecase.inventory

import com.zera.android.model.dto.inventory.DisposalScheduleSummaryDTO
import com.zera.android.model.remote.client.InventoryClient
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ListDisposalSchedules {
    suspend fun execute(now: LocalDateTime = LocalDateTime.now()): List<DisposalScheduleSummaryDTO> {
        val page = InventoryClient.inventoryService.getDisposalSchedules(
            from = now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
        )
        return page.content
    }
}
