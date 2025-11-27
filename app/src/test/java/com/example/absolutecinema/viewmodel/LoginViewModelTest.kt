package com.example.absolutecinema.viewmodel

import com.example.absolutecinema.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.clearMocks
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertEquals

// TODO: Replace these placeholders with your actual classes or package names
interface AuthRepository {
    suspend fun login(email: String, password: String): Result<String>
}

class LoginViewModel(private val repo: AuthRepository) {
    var lastResult: Result<String>? = null
    suspend fun login(email: String, pw: String) {
        lastResult = repo.login(email, pw)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    // Ensure this rule class file is present in app/src/test/java/... and package matches the import
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repo = mockk<AuthRepository>()
    private lateinit var vm: LoginViewModel

    @Before
    fun setup() {
        vm = LoginViewModel(repo)
    }

    @After
    fun tearDown() {
        clearMocks(repo)
    }

    @Test
    fun login_success_updates_state() = runTest {
        coEvery { repo.login("abdoharb01000@gmail.com", "123456789") } returns Result.success("uid123")
        vm.login("abdoharb01000@gmail.com", "123456789")
        assertEquals(true, vm.lastResult?.isSuccess)
        assertEquals("uid123", vm.lastResult?.getOrNull())
    }

    @Test
    fun login_failure_maps_error() = runTest {
        val ex = Exception("bad creds")
        coEvery { repo.login("a@b.com", "bad") } returns Result.failure(ex)
        vm.login("a@b.com", "bad")
        assertEquals(true, vm.lastResult?.isFailure)
    }
}