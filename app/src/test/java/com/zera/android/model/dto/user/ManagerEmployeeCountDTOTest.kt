package com.zera.android.model.dto.user

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class ManagerEmployeeCountDTOTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun parsesCountByManagerRow() {
        val payload = """{ "managerId": "aa11bb22-0000-0000-0000-000000000009", "count": 43 }"""
        val row = json.decodeFromString<ManagerEmployeeCountDTO>(payload)
        assertEquals("aa11bb22-0000-0000-0000-000000000009", row.managerId)
        assertEquals(43, row.count)
    }
}
