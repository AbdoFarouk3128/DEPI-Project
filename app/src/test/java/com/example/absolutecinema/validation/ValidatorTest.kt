package com.example.absolutecinema.validation

import org.junit.Assert.*
import org.junit.Test

// Pure-JVM validator for tests (avoids android.util.Patterns)
object Validator {
    private val EMAIL_REGEX =
        ("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}\$").toRegex(RegexOption.IGNORE_CASE)

    fun isValidEmail(email: String?): Boolean {
        if (email.isNullOrBlank()) return false
        return EMAIL_REGEX.matches(email)
    }

    fun isValidPassword(pw: String?): Boolean {
        if (pw.isNullOrBlank()) return false
        return pw.length >= 6
    }
}

class ValidatorTest {

    @Test
    fun `valid emails return true`() {
        assertTrue(Validator.isValidEmail("user@example.com"))
        assertTrue(Validator.isValidEmail("first.last@domain.co"))
    }

    @Test
    fun `invalid emails return false`() {
        assertFalse(Validator.isValidEmail("not-an-email"))
        assertFalse(Validator.isValidEmail(""))
        assertFalse(Validator.isValidEmail(null))
    }

    @Test
    fun `password validation`() {
        assertTrue(Validator.isValidPassword("123456"))
        assertFalse(Validator.isValidPassword("123"))
        assertFalse(Validator.isValidPassword(null))
    }
}