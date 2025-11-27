package com.example.absolutecinema.repository

import org.junit.Assert.*
import org.junit.Test

// Example Fake repository test — adapt to your repository interface / mapping logic.
class FakeAuthRepository {
    private val users = mutableMapOf<String, String>() // email -> password
    fun register(email: String, password: String) {
        users[email] = password
    }
    fun login(email: String, password: String): Boolean {
        return users[email] == password
    }
}

class AuthRepositoryTest {

    @Test
    fun `fake register and login success`() {
        val repo = FakeAuthRepository()
        repo.register("a@b.com", "pw")
        assertTrue(repo.login("a@b.com", "pw"))
    }

    @Test
    fun `login fails with wrong password`() {
        val repo = FakeAuthRepository()
        repo.register("a@b.com", "pw")
        assertFalse(repo.login("a@b.com", "wrong"))
    }

    @Test
    fun `login fails when user not found`() {
        val repo = FakeAuthRepository()
        assertFalse(repo.login("noone@x.com", "pw"))
    }
}