package com.zera.android.model.remote.client

import okhttp3.HttpUrl.Companion.toHttpUrl
import org.junit.Assert.assertEquals
import org.junit.Test

class InventoryClientUrlTest {
    @Test
    fun kongPrefixResolvesToDashboardHome() {
        val base = serviceBaseUrl("https://34.95.129.59/qa/inventory/", stripApiV1 = true)
        val home = base.toHttpUrl().resolve("api/v1/dashboard/home")!!.toString()

        assertEquals("https://34.95.129.59/qa/inventory/", base)
        assertEquals("https://34.95.129.59/qa/inventory/api/v1/dashboard/home", home)
    }

    @Test
    fun apiUrlWithApiV1DoesNotDuplicateTheVersionSegment() {
        val base = serviceBaseUrl("http://34.95.129.59/qa/inventory/api/v1", stripApiV1 = true)
        val home = base.toHttpUrl().resolve("api/v1/dashboard/home")!!.toString()

        assertEquals("http://34.95.129.59/qa/inventory/", base)
        assertEquals("http://34.95.129.59/qa/inventory/api/v1/dashboard/home", home)
    }

    @Test
    fun directHostResolvesToDashboardHome() {
        val base = serviceBaseUrl("https://inventory.example.com", stripApiV1 = true)
        val home = base.toHttpUrl().resolve("api/v1/dashboard/home")!!.toString()

        assertEquals("https://inventory.example.com/", base)
        assertEquals("https://inventory.example.com/api/v1/dashboard/home", home)
    }
}
