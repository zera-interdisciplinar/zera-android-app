package com.zera.android.viewmodel.manager

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.zera.android.model.usecase.ai.GenerateDisposalReport
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.viewmodel.ZeraViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.net.URLEncoder

data class SchedulingSuccessState(
    val recyclerName: String = "",
    val scheduledAt: String = "",
    val materials: String = "",
    val contactEmail: String = "",
    val contactPhone: String = "",
    val itemNames: List<String> = emptyList(),
    val disposalId: String = "",
    val pendingUri: String? = null,
    val isGeneratingReport: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

class SchedulingSuccessViewModel : ZeraViewModel() {
    private val generateDisposalReport = GenerateDisposalReport()
    private val _state = mutableStateOf(SchedulingSuccessState())
    val state = _state

    private var boundKey: String? = null

    fun bind(
        recyclerName: String,
        scheduledAt: String,
        materials: String,
        contactEmail: String,
        contactPhone: String,
        itemNames: List<String>,
        disposalId: String,
    ) {
        val key = disposalId.ifBlank { "$recyclerName|$scheduledAt" }
        if (boundKey == key) return
        boundKey = key
        _state.value = SchedulingSuccessState(
            recyclerName = recyclerName,
            scheduledAt = scheduledAt,
            materials = materials,
            contactEmail = contactEmail,
            contactPhone = contactPhone,
            itemNames = itemNames,
            disposalId = disposalId,
        )
    }

    fun onEmailClick() {
        val current = _state.value
        val email = current.contactEmail.trim()
        if (email.isEmpty()) {
            _state.value = current.copy(errorMessage = "E-mail da recicladora não cadastrado.")
            return
        }
        val body = contactMessage(current.recyclerName, current.itemNames)
        _state.value = current.copy(
            pendingUri = mailtoUri(email, EMAIL_SUBJECT, body),
            errorMessage = null,
        )
    }

    fun onPhoneClick() {
        val current = _state.value
        val phone = current.contactPhone.trim()
        if (phone.isEmpty()) {
            _state.value = current.copy(errorMessage = "Telefone da recicladora não cadastrado.")
            return
        }
        _state.value = current.copy(
            pendingUri = whatsappUri(phone, contactMessage(current.recyclerName, current.itemNames)),
            errorMessage = null,
        )
    }

    fun consumePendingUri() {
        _state.value = _state.value.copy(pendingUri = null)
    }

    fun onCloseClick() {
        ZeraNavigator.pushAndPopAll(Route.ManagerHome)
    }

    fun onBackToHomeClick() {
        ZeraNavigator.pushAndPopAll(Route.ManagerHome)
    }

    fun onGenerateReport() {
        val current = _state.value
        if (current.isGeneratingReport) return
        _state.value = current.copy(isGeneratingReport = true, errorMessage = null)
        viewModelScope.launch {
            try {
                val reportUrl = generateDisposalReport.execute(current.disposalId)
                _state.value = _state.value.copy(
                    isGeneratingReport = false,
                    pendingUri = reportUrl,
                    errorMessage = null,
                )
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _state.value = _state.value.copy(
                    isGeneratingReport = false,
                    errorMessage = error.message ?: "Não foi possível gerar o relatório.",
                )
            }
        }
    }

    companion object {
        internal const val EMAIL_SUBJECT = "Registro de descarte de eletrônicos"

        internal fun contactMessage(recyclerName: String, itemNames: List<String>): String {
            val who = recyclerName.ifBlank { "equipe" }
            val items = if (itemNames.isEmpty()) {
                "- (itens registrados no app Zera)"
            } else {
                itemNames.joinToString("\n") { "- $it" }
            }
            return """
                Olá, $who!

                Registrei um descarte de resíduos eletrônicos destinado à sua cooperativa.

                Itens:
                $items

                Fico no aguardo.
            """.trimIndent()
        }

        internal fun whatsappUri(phone: String, message: String): String {
            val digits = phone.filter { it.isDigit() }
            val withCountry = if (digits.startsWith("55")) digits else "55$digits"
            return "https://wa.me/$withCountry?text=${encodeQuery(message)}"
        }

        internal fun mailtoUri(email: String, subject: String, body: String): String {
            return "mailto:$email" +
                "?subject=${encodeQuery(subject)}" +
                "&body=${encodeQuery(body)}"
        }

        private fun encodeQuery(value: String): String =
            URLEncoder.encode(value, "UTF-8").replace("+", "%20")
    }
}
