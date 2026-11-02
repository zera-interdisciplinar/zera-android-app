package com.zera.android.viewmodel.auth

import kotlinx.coroutines.awaitCancellation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

// Robolectric fornece o Dispatchers.Main usado pelo viewModelScope.
@RunWith(RobolectricTestRunner::class)
class SignUpViewModelTest {
    private fun viewModel(
        token: String = "123456",
        redeem: suspend () -> Unit,
    ) = SignUpViewModel(redeemInvitation = { _, _, _, _ -> redeem() }).apply {
        onNameChange("Carol")
        onEmailChange("carol@empresa.com")
        onPasswordChange("segredo123")
        onTokenChange(token)
    }

    @Test
    fun secondSignUpWhileLoadingIsIgnored() {
        var calls = 0
        val viewModel = viewModel(
            redeem = {
                calls++
                awaitCancellation()
            },
        )

        viewModel.signUp()
        viewModel.signUp()

        assertEquals(1, calls)
        assertTrue(viewModel.state.value.isLoading)
    }

    @Test
    fun failureAllowsTryingAgain() {
        var calls = 0
        val viewModel = viewModel(
            redeem = {
                calls++
                error("falha")
            },
        )

        viewModel.signUp()
        assertFalse(viewModel.state.value.isLoading)

        viewModel.signUp()

        assertEquals(2, calls)
    }

    @Test
    fun validationErrorAllowsTryingAgain() {
        var calls = 0
        val viewModel = viewModel(
            token = "123",
            redeem = {
                calls++
                awaitCancellation()
            },
        )

        viewModel.signUp()
        assertEquals(0, calls)
        assertFalse(viewModel.state.value.isLoading)

        viewModel.onTokenChange("123456")
        viewModel.signUp()

        assertEquals(1, calls)
    }
}
