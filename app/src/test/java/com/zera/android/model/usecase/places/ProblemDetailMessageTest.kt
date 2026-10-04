package com.zera.android.model.usecase.places

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ProblemDetailMessageTest {
    @Test
    fun readsDetailFromProblemJson() {
        val body = """{"detail":"Busca de recicladoras proximas esta desativada nesta instancia"}"""
        assertEquals(
            "Busca de recicladoras proximas esta desativada nesta instancia",
            problemDetailMessage(body),
        )
    }

    @Test
    fun returnsNullWhenBodyIsNotProblemDetail() {
        assertNull(problemDetailMessage("not-json"))
        assertNull(problemDetailMessage(null))
    }
}
