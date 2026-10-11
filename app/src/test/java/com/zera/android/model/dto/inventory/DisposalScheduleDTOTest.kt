package com.zera.android.model.dto.inventory

import com.zera.android.viewmodel.manager.ScheduledDisposalsViewModel
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class DisposalScheduleDTOTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun decodesDisposalDetailWithItems() {
        val detail = json.decodeFromString(
            DisposalRecordDTO.serializer(),
            """
            {
              "id": "disp-1",
              "destination": "RECYCLING",
              "placeId": "places/ChIJ",
              "placeName": "Ecoponto Central",
              "disposedAt": "2026-09-28",
              "items": [{ "id": "item-1", "displayCode": "265964", "name": "Notebook" }]
            }
            """.trimIndent(),
        )

        assertEquals("disp-1", detail.id)
        assertEquals("2026-09-28", detail.disposedAt)
        assertEquals("Notebook", detail.itemLabels().single())
        assertEquals(1, detail.itemCount())
    }

    @Test
    fun decodesNullPlaceId() {
        val detail = json.decodeFromString(
            DisposalRecordDTO.serializer(),
            """
            { "id": "disp-3", "destination": "DONATION", "placeId": null, "placeName": "ONG Recicla" }
            """.trimIndent(),
        )

        assertEquals(null, detail.placeId)
        assertEquals("ONG Recicla", detail.placeName)
    }

    @Test
    fun countsItemIdsWhenItemsAreAbsent() {
        val detail = json.decodeFromString(
            DisposalRecordDTO.serializer(),
            """
            { "id": "disp-2", "itemIds": ["a", "b"] }
            """.trimIndent(),
        )

        assertEquals(2, detail.itemCount())
    }

    @Test
    fun formatsDisposedAtAsDayAndMonth() {
        assertEquals("28 set", ScheduledDisposalsViewModel.formatDisposedAt("2026-09-28"))
        assertEquals("28 set", ScheduledDisposalsViewModel.formatDisposedAt("2026-09-28T15:00:00"))
    }

    @Test
    fun buildsServerQueriesFromChips() {
        val today = LocalDate.parse("2026-10-10")
        val queries = ScheduledDisposalsViewModel.queriesFor(
            searchQuery = " notebook ",
            destinations = listOf("Reciclagem", "Doação"),
            period = "Últimos 7 dias",
            today = today,
        )

        assertEquals(2, queries.size)
        assertEquals("notebook", queries[0].q)
        assertEquals("RECYCLING", queries[0].destination)
        assertEquals("DONATION", queries[1].destination)
        assertEquals("2026-10-04", queries[0].disposedFrom)
        assertEquals("2026-10-10", queries[0].disposedTo)
    }

    @Test
    fun omitsFiltersWhenChipsAreEmpty() {
        val query = ScheduledDisposalsViewModel.queriesFor(
            searchQuery = "  ",
            destinations = emptyList(),
            period = null,
            today = LocalDate.parse("2026-10-10"),
        ).single()

        assertNull(query.q)
        assertNull(query.destination)
        assertNull(query.disposedFrom)
        assertNull(query.disposedTo)
    }

    @Test
    fun loadsNextPageOnlyWhilePagesRemain() {
        assertFalse(ScheduledDisposalsViewModel.canLoadMore(0, 1, isBusy = false))
        assertTrue(ScheduledDisposalsViewModel.canLoadMore(0, 3, isBusy = false))
        assertFalse(ScheduledDisposalsViewModel.canLoadMore(0, 3, isBusy = true))
    }
}
