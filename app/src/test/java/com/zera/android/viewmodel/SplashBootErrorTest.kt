package com.zera.android.viewmodel

import com.zera.android.view.navigation.Route
import kotlinx.serialization.SerializationException
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.net.UnknownHostException

class SplashBootErrorTest {

    @Test
    fun connectionFailureOpensConnectionErrorFromBoot() {
        assertEquals(
            Route.ConnectionError(fromBoot = true),
            SplashViewModel.bootErrorRoute(UnknownHostException("scrapy")),
        )
    }

    @Test
    fun serverFailureOpensFatalError() {
        val serverError = HttpException(Response.error<Unit>(503, "".toResponseBody()))

        assertEquals(Route.FatalError, SplashViewModel.bootErrorRoute(serverError))
    }

    @Test
    fun invalidConfigOpensFatalError() {
        assertEquals(Route.FatalError, SplashViewModel.bootErrorRoute(SerializationException("bad")))
    }
}
