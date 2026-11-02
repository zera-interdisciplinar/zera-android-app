package com.zera.android.view.components.lists

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.zera.android.view.components.setZeraContent
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EmployeeListTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val pending = EmployeeItem(id = "1", name = "Carol", role = "Operador", isPending = true)
    private val active = EmployeeItem(id = "2", name = "Kevin", role = "Gestor")

    @Test
    fun showsTheEmployeesAndTheirStatus() {
        composeRule.setZeraContent {
            EmployeeList(employees = listOf(pending, active), onItemClick = {})
        }

        composeRule.onNodeWithText("Carol").assertIsDisplayed()
        composeRule.onNodeWithText("Operador · Pendente").assertIsDisplayed()
        composeRule.onNodeWithText("Kevin").assertIsDisplayed()
        composeRule.onNodeWithText("Gestor · Ativo").assertIsDisplayed()
    }

    @Test
    fun clickSendsTheEmployee() {
        val clicks = mutableListOf<EmployeeItem>()
        composeRule.setZeraContent {
            EmployeeList(employees = listOf(pending, active), onItemClick = { clicks += it })
        }

        composeRule.onNodeWithText("Kevin").performClick()

        assertEquals(listOf(active), clicks)
    }

    @Test
    fun emptyListShowsTheDefaultMessage() {
        composeRule.setZeraContent {
            EmployeeList(employees = emptyList(), onItemClick = {})
        }

        composeRule.onNodeWithText("Nenhum colaborador encontrado").assertIsDisplayed()
    }
}
