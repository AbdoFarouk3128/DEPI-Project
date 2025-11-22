package com.example.absolutecinema.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.absolutecinema.util.getOrAwaitValue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Tests the LOCAL toggle behavior of LikedMoviesViewModel.
 */
class LikedMoviesViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var viewModel: LikedMoviesViewModel

    @Before
    fun setup() {
        mockkStatic(FirebaseAuth::class)
        mockkStatic(FirebaseFirestore::class)

        val mockAuth = mockk<FirebaseAuth>(relaxed = true)
        every { FirebaseAuth.getInstance() } returns mockAuth
        every { mockAuth.currentUser?.uid } returns "testUid"

        val mockFirestore = mockk<FirebaseFirestore>(relaxed = true)
        every { FirebaseFirestore.getInstance() } returns mockFirestore

        val users = mockk<CollectionReference>(relaxed = true)
        val userDoc = mockk<DocumentReference>(relaxed = true)
        val liked = mockk<CollectionReference>(relaxed = true)
        val likedDoc = mockk<DocumentReference>(relaxed = true)

        every { mockFirestore.collection("users") } returns users
        every { users.document("testUid") } returns userDoc
        every { userDoc.collection("liked") } returns liked
        every { liked.document(any()) } returns likedDoc

        viewModel = LikedMoviesViewModel()
    }

    @Test
    fun `likedListControl toggles presence`() {
        val id = "99"
        val poster = "/a.png"

        // add
        viewModel.likedListControl(id, poster)
        assertEquals(1, viewModel.likedList.getOrAwaitValue().size)

        // remove
        viewModel.likedListControl(id, poster)
        assertEquals(0, viewModel.likedList.getOrAwaitValue().size)
    }
}