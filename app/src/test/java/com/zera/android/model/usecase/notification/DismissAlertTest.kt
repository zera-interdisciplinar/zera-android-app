package com.zera.android.model.usecase.notification

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.util.UUID

class DismissAlertTest {
    private val alertId = UUID.randomUUID().toString()

    @Test
    fun closesTheAlertAndKeepsItDismissed() = runBlocking {
        val closed = mutableListOf<String>()

        DismissAlert(closeAlert = { closed += it }).execute(alertId)

        assertEquals(listOf(alertId), closed)
        assertTrue(alertId in DismissedAlerts.ids.value)
    }

    @Test
    fun marksAsDismissedBeforeTheCallFinishes() = runBlocking {
        val release = CompletableDeferred<Unit>()
        val started = CompletableDeferred<Unit>()
        val dismiss = async(Dispatchers.Default) {
            DismissAlert(
                closeAlert = {
                    started.complete(Unit)
                    release.await()
                },
            ).execute(alertId)
        }

        started.await()
        assertTrue(alertId in DismissedAlerts.ids.value)

        release.complete(Unit)
        dismiss.await()
    }

    @Test
    fun serverErrorBringsTheAlertBackAndRethrows() {
        val error = httpError(500)

        val thrown = assertThrows(HttpException::class.java) {
            runBlocking { DismissAlert(closeAlert = { throw error }).execute(alertId) }
        }

        assertEquals(error, thrown)
        assertFalse(alertId in DismissedAlerts.ids.value)
    }

    @Test
    fun connectionErrorBringsTheAlertBackAndRethrows() {
        assertThrows(IOException::class.java) {
            runBlocking { DismissAlert(closeAlert = { throw IOException() }).execute(alertId) }
        }

        assertFalse(alertId in DismissedAlerts.ids.value)
    }

    @Test
    fun notFoundCountsAsSuccess() = runBlocking {
        DismissAlert(closeAlert = { throw httpError(404) }).execute(alertId)

        assertTrue(alertId in DismissedAlerts.ids.value)
    }

    @Test
    fun cancellingTheCallerDoesNotCancelTheClose() = runBlocking {
        val release = CompletableDeferred<Unit>()
        val started = CompletableDeferred<Unit>()
        val finished = CompletableDeferred<Unit>()
        val caller = launch(Dispatchers.Default, start = CoroutineStart.UNDISPATCHED) {
            DismissAlert(
                closeAlert = {
                    started.complete(Unit)
                    release.await()
                    finished.complete(Unit)
                },
            ).execute(alertId)
        }

        started.await()
        caller.cancel()
        release.complete(Unit)
        caller.join()

        assertTrue(finished.isCompleted)
        assertTrue(alertId in DismissedAlerts.ids.value)
    }

    private fun httpError(code: Int) =
        HttpException(Response.error<Unit>(code, "".toResponseBody()))
}
