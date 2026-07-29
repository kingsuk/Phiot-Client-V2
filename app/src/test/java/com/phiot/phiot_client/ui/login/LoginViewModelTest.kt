package com.phiot.phiot_client.ui.login

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LoginViewModelTest {

    @Test
    fun emailRegex_acceptsValidAddress() {
        assertTrue(LoginViewModel.EMAIL_REGEX.matches("user@phiot.com"))
    }

    @Test
    fun emailRegex_rejectsInvalidAddress() {
        assertFalse(LoginViewModel.EMAIL_REGEX.matches("not-an-email"))
    }
}
