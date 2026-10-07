package com.zera.android.viewmodel.manager

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SchedulingSuccessContactTest {
    @Test
    fun contactMessageListsSelectedItems() {
        val message = SchedulingSuccessViewModel.contactMessage(
            recyclerName = "Cooperativa Recicla SP",
            itemNames = listOf("Notebook", "Bateria"),
        )

        assertTrue(message.contains("Cooperativa Recicla SP"))
        assertTrue(message.contains("- Notebook"))
        assertTrue(message.contains("- Bateria"))
        assertTrue(message.contains("Registrei um descarte"))
    }

    @Test
    fun whatsappUriAddsBrazilCountryCode() {
        val uri = SchedulingSuccessViewModel.whatsappUri("11987654321", "olá")

        assertEquals("https://wa.me/5511987654321?text=ol%C3%A1", uri)
    }

    @Test
    fun whatsappUriKeepsExistingCountryCode() {
        val uri = SchedulingSuccessViewModel.whatsappUri("5511987654321", "oi")

        assertTrue(uri.startsWith("https://wa.me/5511987654321?"))
    }

    @Test
    fun mailtoUriEncodesSubjectAndBody() {
        val uri = SchedulingSuccessViewModel.mailtoUri(
            email = "contato@recicla.com",
            subject = "Orçamento",
            body = "linha 1\nlinha 2",
        )

        assertTrue(uri.startsWith("mailto:contato@recicla.com?"))
        assertTrue(uri.contains("subject=Or%C3%A7amento"))
        assertTrue(uri.contains("body=linha%201%0Alinha%202"))
    }
}
