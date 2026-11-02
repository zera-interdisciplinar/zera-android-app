package com.zera.android.model.remote.client

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.io.IOException
import java.net.UnknownHostException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class ConnectionErrorInterceptorTest {
    private val request = Request.Builder().url("https://api.example.com/items").build()

    @Test
    fun connectionFailureNotifiesAndRethrows() {
        var lost = 0
        val client = client(ConnectionErrorGate(), failure = UnknownHostException("api")) { lost++ }

        assertThrows(UnknownHostException::class.java) { client.newCall(request).execute() }
        assertEquals(1, lost)
    }

    @Test
    fun otherIoFailureDoesNotNotify() {
        var lost = 0
        val client = client(ConnectionErrorGate(), failure = IOException("stream closed")) { lost++ }

        assertThrows(IOException::class.java) { client.newCall(request).execute() }
        assertEquals(0, lost)
    }

    @Test
    fun parallelFailuresNotifyOnce() {
        val lost = AtomicInteger(0)
        val client = client(ConnectionErrorGate(), failure = UnknownHostException("api")) {
            lost.incrementAndGet()
        }
        val pool = Executors.newFixedThreadPool(8)

        repeat(8) {
            pool.execute { runCatching { client.newCall(request).execute() } }
        }
        pool.shutdown()
        pool.awaitTermination(5, TimeUnit.SECONDS)

        assertEquals(1, lost.get())
    }

    @Test
    fun notifiesAgainAfterRelease() {
        var lost = 0
        val gate = ConnectionErrorGate()
        val client = client(gate, failure = UnknownHostException("api")) { lost++ }

        runCatching { client.newCall(request).execute() }
        gate.release()
        runCatching { client.newCall(request).execute() }

        assertEquals(2, lost)
    }

    @Test
    fun suppressedGateDoesNotNotify() {
        var lost = 0
        val gate = ConnectionErrorGate().apply { suppress(true) }
        val client = client(gate, failure = UnknownHostException("api")) { lost++ }

        assertThrows(UnknownHostException::class.java) { client.newCall(request).execute() }
        assertEquals(0, lost)

        gate.suppress(false)
        runCatching { client.newCall(request).execute() }
        assertEquals(1, lost)
    }

    private fun client(
        gate: ConnectionErrorGate,
        failure: IOException,
        onConnectionLost: () -> Unit,
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(ConnectionErrorInterceptor(gate, onConnectionLost))
        .addInterceptor(Interceptor { throw failure })
        .build()
}
