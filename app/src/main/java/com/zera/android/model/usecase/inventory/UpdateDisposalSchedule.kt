package com.zera.android.model.usecase.inventory

import com.zera.android.model.dto.inventory.DisposalScheduleDetailDTO
import com.zera.android.model.dto.inventory.PatchDisposalScheduleRequestDTO
import com.zera.android.model.remote.client.InventoryClient
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class UpdateDisposalSchedule {
    suspend fun execute(id: String, scheduledAt: LocalDateTime): DisposalScheduleDetailDTO {
        require(scheduledAt.isAfter(LocalDateTime.now())) { "Escolha uma data e hora futuras." }
        return InventoryClient.inventoryService.patchDisposalSchedule(
            id = id.trim(),
            body = PatchDisposalScheduleRequestDTO(
                scheduledAt = scheduledAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            ),
        )
    }
}
