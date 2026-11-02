package com.zera.android.view.components.forms

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.isSelectable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextReplacement
import com.zera.android.view.components.setZeraContent
import org.junit.Assert.assertEquals
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EditItemFormTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val nameChanges = mutableListOf<String>()
    private val conditionChanges = mutableListOf<String>()
    private val intensityChanges = mutableListOf<Int>()
    private val hasDamageChanges = mutableListOf<Boolean>()
    private val damageChanges = mutableListOf<String>()
    private val notesChanges = mutableListOf<String>()
    private var saves = 0
    private var cancels = 0

    private val slider = SemanticsMatcher.keyIsDefined(SemanticsActions.SetProgress)

    private fun showForm(
        hasDamage: Boolean = true,
        nameError: String? = null,
        damageTypeError: String? = null,
        notesError: String? = null,
        errorMessage: String? = null,
        isSubmitting: Boolean = false,
    ) {
        composeRule.setZeraContent {
            EditItemForm(
                name = "Placa de vídeo",
                onNameChange = { nameChanges += it },
                condition = "Usado",
                conditionOptions = listOf("Novo", "Usado"),
                onConditionChange = { conditionChanges += it },
                usageIntensity = 4,
                onUsageIntensityChange = { intensityChanges += it },
                hasDamage = hasDamage,
                onHasDamageChange = { hasDamageChanges += it },
                damageType = "Tela quebrada",
                damageOptions = listOf("Tela quebrada", "Não liga"),
                onDamageTypeChange = { damageChanges += it },
                notes = "Arranhão na lateral",
                onNotesChange = { notesChanges += it },
                onSave = { saves++ },
                onCancel = { cancels++ },
                nameError = nameError,
                damageTypeError = damageTypeError,
                notesError = notesError,
                errorMessage = errorMessage,
                isSubmitting = isSubmitting,
            )
        }
    }

    @Test
    fun showsTheValues() {
        showForm()

        composeRule.onNodeWithText("Placa de vídeo").assertIsDisplayed()
        composeRule.onNodeWithText("Usado").assertIsDisplayed()
        composeRule.onNodeWithText("Tela quebrada").assertIsDisplayed()
        composeRule.onNodeWithText("Arranhão na lateral").assertIsDisplayed()
    }

    @Test
    fun damageTypeAppearsOnlyWithDamage() {
        showForm(hasDamage = false)

        composeRule.onNodeWithText("TIPO DE DANO").assertDoesNotExist()
        composeRule.onNodeWithText("Tela quebrada").assertDoesNotExist()
    }

    @Test
    fun radioSendsTheCurrentValue() {
        showForm(hasDamage = true)

        composeRule.onNode(isSelectable()).performClick()

        assertEquals(listOf(true), hasDamageChanges)
    }

    @Test
    fun editingTheNameCallsOnlyItsCallback() {
        showForm()

        composeRule.onNodeWithText("Placa de vídeo").performTextReplacement("Monitor")

        assertEquals(listOf("Monitor"), nameChanges)
        assertEquals(emptyList<String>(), notesChanges)
    }

    @Test
    fun editingTheNotesCallsOnlyItsCallback() {
        showForm()

        composeRule.onNodeWithText("Arranhão na lateral").performTextReplacement("Sem marcas")

        assertEquals(listOf("Sem marcas"), notesChanges)
        assertEquals(emptyList<String>(), nameChanges)
    }

    @Test
    fun choosingAConditionCallsOnConditionChange() {
        showForm()

        composeRule.onNodeWithText("Usado").performClick()
        composeRule.onNodeWithText("Novo").performClick()

        assertEquals(listOf("Novo"), conditionChanges)
    }

    @Test
    fun choosingADamageCallsOnDamageTypeChange() {
        showForm()

        composeRule.onNodeWithText("Tela quebrada").performClick()
        composeRule.onNodeWithText("Não liga").performClick()

        assertEquals(listOf("Não liga"), damageChanges)
    }

    @Test
    fun changingTheSliderCallsOnUsageIntensityChange() {
        showForm()

        composeRule.onNode(slider).performSemanticsAction(SemanticsActions.SetProgress) { it(7f) }

        assertEquals(listOf(7), intensityChanges)
    }

    @Test
    fun showsTheFieldErrorsAndTheGeneralError() {
        showForm(
            nameError = "Nome inválido",
            damageTypeError = "Selecione o dano",
            notesError = "Observação inválida",
            errorMessage = "Não foi possível salvar",
        )

        composeRule.onNodeWithText("Nome inválido").assertIsDisplayed()
        composeRule.onNodeWithText("Selecione o dano").assertIsDisplayed()
        composeRule.onNodeWithText("Observação inválida").assertIsDisplayed()
        composeRule.onNodeWithText("Não foi possível salvar").assertIsDisplayed()
    }

    @Test
    fun saveAndCancelCallTheirCallbacks() {
        showForm()

        composeRule.onNodeWithText("Salvar").performClick()
        assertEquals(1, saves)
        assertEquals(0, cancels)

        composeRule.onNodeWithText("Cancelar").performClick()
        assertEquals(1, saves)
        assertEquals(1, cancels)
    }

    @Test
    fun submittingBlocksTheForm() {
        showForm(isSubmitting = true)

        composeRule.onNodeWithText("Placa de vídeo").assertIsNotEnabled()
        composeRule.onNodeWithText("Arranhão na lateral").assertIsNotEnabled()
        composeRule.onNode(slider).assertIsNotEnabled()
        composeRule.onNodeWithText("Salvar").assertDoesNotExist()
        composeRule.onNodeWithText("Salvando...").assertIsNotEnabled().performClick()
        composeRule.onNodeWithText("Cancelar").assertIsNotEnabled().performClick()
        assertEquals(0, saves)
        assertEquals(0, cancels)
    }

    @Test
    fun submittingKeepsTheDropdownsClosed() {
        showForm(isSubmitting = true)

        composeRule.onNodeWithText("Usado").performClick()

        composeRule.onNodeWithText("Novo").assertDoesNotExist()
    }

    @Ignore("Problema conhecido 2 da spec testes-componentes-visuais: o radio segue ativo durante o envio")
    @Test
    fun submittingBlocksTheRadio() {
        showForm(isSubmitting = true)

        composeRule.onNode(isSelectable()).performClick()

        assertEquals(emptyList<Boolean>(), hasDamageChanges)
    }
}
