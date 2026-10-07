package com.zera.android.viewmodel.manager

import org.junit.Assert.assertEquals
import org.junit.Test

class ItensResumeMappingTest {
    @Test
    fun zipStopsAtTheShorterList() {
        val items = ItensResumeViewModel.zipSelectedItems(
            itemIds = listOf("1", "2", "3"),
            itemNames = listOf("Notebook", "Mouse"),
        )

        assertEquals(listOf("1", "2"), items.map { it.id })
        assertEquals(listOf("Notebook", "Mouse"), items.map { it.name })
    }
}
