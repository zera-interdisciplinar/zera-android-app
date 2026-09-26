package com.zera.android.model.remote.client

import okhttp3.HttpUrl.Companion.toHttpUrl
import org.junit.Assert.assertEquals
import org.junit.Test

class ScrapyClientUrlTest {
    @Test
    fun kongPrefixResolvesToV1BootAndFlags() {
        val base = ScrapyClient.apiBaseUrl("https://34.95.129.59/qa/scrapy/")
        val boot = base.toHttpUrl().resolve("v1/boot")!!.toString()
        val flags = base.toHttpUrl().resolve("v1/flags")!!.toString()

        assertEquals("https://34.95.129.59/qa/scrapy/", base)
        assertEquals("https://34.95.129.59/qa/scrapy/v1/boot", boot)
        assertEquals("https://34.95.129.59/qa/scrapy/v1/flags", flags)
    }

    @Test
    fun apiUrlWithV1DoesNotDuplicateTheVersionSegment() {
        val base = ScrapyClient.apiBaseUrl("http://34.95.129.59/qa/scrapy/v1")
        val boot = base.toHttpUrl().resolve("v1/boot")!!.toString()

        assertEquals("http://34.95.129.59/qa/scrapy/", base)
        assertEquals("http://34.95.129.59/qa/scrapy/v1/boot", boot)
    }

    @Test
    fun directServiceUrlResolvesToV1Boot() {
        val base = ScrapyClient.apiBaseUrl("https://host/v1/")
        val boot = base.toHttpUrl().resolve("v1/boot")!!.toString()

        assertEquals("https://host/", base)
        assertEquals("https://host/v1/boot", boot)
    }
}
