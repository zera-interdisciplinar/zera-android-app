package com.zera.android.viewmodel.employee

import com.zera.android.model.dto.inventory.CreateItemRequestDTO
import com.zera.android.model.dto.inventory.ItemResponseDTO
import com.zera.android.model.dto.inventory.ModelResponseDTO
import com.zera.android.model.dto.inventory.PagedModelsDTO
import com.zera.android.model.usecase.inventory.CreateItem
import com.zera.android.model.usecase.inventory.GetModels
import com.zera.android.model.usecase.inventory.SubmitItem
import com.zera.android.view.navigation.NavCommand
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class ManualRegisterViewModelTest {
    @Before
    fun drainPendingNavCommands() = runBlocking {
        while (withTimeoutOrNull(50) { ZeraNavigator.commands.first() } != null) {
            // esvazia o canal compartilhado entre testes
        }
    }

    @Test
    fun startsWithBarcodeWhenProvided() {
        val viewModel = registerViewModel(initialBarcode = "7891234567890")
        assertEquals("7891234567890", viewModel.state.value.barcode)
    }

    @Test
    fun startsWithoutBarcodeWhenOmitted() {
        val viewModel = registerViewModel()
        assertEquals("", viewModel.state.value.barcode)
    }

    @Test
    fun loadsModelNamesFromBack() {
        val viewModel = registerViewModel(
            models = listOf(
                ModelResponseDTO(id = "m1", name = "Teclado mecânico"),
                ModelResponseDTO(id = "m2", name = "Mouse sem fio"),
            ),
        )

        assertEquals(listOf("Teclado mecânico", "Mouse sem fio"), viewModel.state.value.modelOptions)
        assertFalse(viewModel.state.value.isLoadingModels)
        assertNull(viewModel.state.value.modelsError)
    }

    @Test
    fun appendsInitialModelWhenMissing() {
        val viewModel = registerViewModel(
            initialModel = "Modelo novo",
            models = listOf(ModelResponseDTO(id = "m1", name = "Teclado mecânico")),
        )

        assertEquals("Modelo novo", viewModel.state.value.model)
        assertEquals(listOf("Teclado mecânico", "Modelo novo"), viewModel.state.value.modelOptions)
        assertNull(viewModel.state.value.selectedModelId)
        assertFalse(viewModel.state.value.canSubmit)
    }

    @Test
    fun keepsBarcodeWhenModelsFail() {
        val viewModel = registerViewModel(
            initialBarcode = "789",
            getModels = GetModels { _, _, _ -> throw RuntimeException("timeout") },
        )

        assertEquals("789", viewModel.state.value.barcode)
        assertEquals("timeout", viewModel.state.value.modelsError)
        assertTrue(viewModel.state.value.modelOptions.isEmpty())
        assertFalse(viewModel.state.value.canSubmit)
    }

    @Test
    fun submitsMappedPayloadAndOpensDetails() = runBlocking {
        var request: CreateItemRequestDTO? = null
        var submittedId: String? = null
        val viewModel = registerViewModel(
            models = listOf(ModelResponseDTO(id = "model-1", name = "Teclado mecânico")),
            createItem = CreateItem { body ->
                request = body
                ItemResponseDTO(id = "item-9", name = body.name)
            },
            submitItem = SubmitItem { id ->
                submittedId = id
                ItemResponseDTO(id = id, name = "Notebook")
            },
        )
        fillValid(viewModel)

        viewModel.onConfirmClick()

        assertEquals(
            CreateItemRequestDTO(
                name = "Notebook",
                modelId = "model-1",
                barcode = null,
                serialNumber = "SN1",
                condition = "USED",
                hasDamages = false,
                damages = emptyList(),
                notes = null,
                acquiredAt = "2026-07-01",
                manufacturingYear = 2022,
                usageIntensity = 1,
            ),
            request,
        )
        assertEquals("item-9", submittedId)
        assertEquals(
            NavCommand.PushAndPop(Route.ItemDetails(itemId = "item-9"), popCount = 1),
            nextNavCommand(),
        )
        return@runBlocking
    }

    @Test
    fun omitsEmptyBarcodeAndSendsDamageCode() = runBlocking {
        var request: CreateItemRequestDTO? = null
        val viewModel = registerViewModel(
            initialBarcode = "  ",
            models = listOf(ModelResponseDTO(id = "model-1", name = "Teclado mecânico")),
            createItem = CreateItem { body ->
                request = body
                ItemResponseDTO(id = "item-1", name = body.name)
            },
        )
        fillValid(viewModel)
        viewModel.onHasDamageChange(false)
        viewModel.onDamageTypeChange("Tela quebrada")

        viewModel.onConfirmClick()

        assertNull(request?.barcode)
        assertEquals(true, request?.hasDamages)
        assertEquals(listOf("BROKEN_SCREEN"), request?.damages)
        nextNavCommand()
        return@runBlocking
    }

    @Test
    fun keepsScreenOnSubmitError() {
        val viewModel = registerViewModel(
            models = listOf(ModelResponseDTO(id = "model-1", name = "Teclado mecânico")),
            createItem = CreateItem { throw httpError(400) },
        )
        fillValid(viewModel)

        viewModel.onConfirmClick()

        assertFalse(viewModel.state.value.isSubmitting)
        assertTrue(viewModel.state.value.submitError != null)
    }

    @Test
    fun keepsScreenWhenSubmitAfterCreateFails() {
        val viewModel = registerViewModel(
            models = listOf(ModelResponseDTO(id = "model-1", name = "Teclado mecânico")),
            createItem = CreateItem { ItemResponseDTO(id = "item-1", name = it.name) },
            submitItem = SubmitItem { throw httpError(422) },
        )
        fillValid(viewModel)

        viewModel.onConfirmClick()

        assertFalse(viewModel.state.value.isSubmitting)
        assertTrue(viewModel.state.value.submitError != null)
    }

    @Test
    fun ignoresSecondSubmitWhileInFlight() = runBlocking {
        var pending: (suspend () -> Unit)? = null
        var launches = 0
        var submits = 0
        val viewModel = ManualRegisterViewModel(
            getModels = GetModels { _, _, _ ->
                PagedModelsDTO(content = listOf(ModelResponseDTO(id = "model-1", name = "Teclado mecânico")))
            },
            createItem = CreateItem {
                submits += 1
                ItemResponseDTO(id = "item-1", name = it.name)
            },
            submitItem = SubmitItem { ItemResponseDTO(id = it, name = "item") },
            launchWork = { block ->
                if (launches++ == 0) {
                    runBlocking { block() }
                } else {
                    pending = block
                }
            },
        )
        fillValid(viewModel)

        viewModel.onConfirmClick()
        viewModel.onConfirmClick()

        assertEquals(2, launches)
        assertEquals(0, submits)
        assertTrue(viewModel.state.value.isSubmitting)

        pending!!()
        assertEquals(1, submits)
        nextNavCommand()
        return@runBlocking
    }

    private fun fillValid(viewModel: ManualRegisterViewModel) {
        viewModel.onSerialNumberChange("SN1")
        viewModel.onItemNameChange("Notebook")
        viewModel.onModelChange("Teclado mecânico")
        viewModel.onManufacturingYearChange("2022")
        viewModel.onAcquisitionDateChange("01/07/2026")
        viewModel.onConditionChange("Usado")
    }

    private fun registerViewModel(
        initialModel: String? = null,
        initialBarcode: String? = null,
        models: List<ModelResponseDTO> = emptyList(),
        getModels: GetModels = GetModels { _, _, _ -> PagedModelsDTO(content = models) },
            createItem: CreateItem = CreateItem { ItemResponseDTO(id = "item-1", name = it.name) },
        submitItem: SubmitItem = SubmitItem { ItemResponseDTO(id = it, name = "item") },
    ) = ManualRegisterViewModel(
        initialModel = initialModel,
        initialBarcode = initialBarcode,
        getModels = getModels,
        createItem = createItem,
        submitItem = submitItem,
        launchWork = { block -> runBlocking { block() } },
    )

    private suspend fun nextNavCommand(): NavCommand =
        withTimeout(1_000) { ZeraNavigator.commands.first() }

    private fun httpError(code: Int): HttpException {
        val body = "{}".toResponseBody("application/json".toMediaType())
        return HttpException(Response.error<String>(code, body))
    }
}
