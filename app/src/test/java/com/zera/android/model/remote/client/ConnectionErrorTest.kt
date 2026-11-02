package com.zera.android.model.remote.client

import kotlinx.serialization.SerializationException
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ConnectionErrorTest {

    @Test
    fun networkFailuresAreConnectionErrors() {
        assertTrue(UnknownHostException("api.zera").isConnectionError())
        assertTrue(ConnectException("refused").isConnectionError())
        assertTrue(SocketTimeoutException("timeout").isConnectionError())
    }

    @Test
    fun wrappedNetworkFailureIsConnectionError() {
        val wrapped = IOException("wrapper", UnknownHostException("api.zera"))

        assertTrue(wrapped.isConnectionError())
    }

    @Test
    fun httpAndParsingFailuresAreNotConnectionErrors() {
        val serverError = HttpException(Response.error<Unit>(500, "".toResponseBody()))

        assertFalse(serverError.isConnectionError())
        assertFalse(SerializationException("bad json").isConnectionError())
        assertFalse(IOException("stream closed").isConnectionError())
        assertFalse(IllegalStateException("bug").isConnectionError())
    }
}
