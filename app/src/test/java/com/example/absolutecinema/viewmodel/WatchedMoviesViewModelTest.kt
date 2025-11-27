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
 * Tests the LOCAL toggle behavior of WatchedMoviesViewModel.
 */
class WatchedMoviesViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var viewModel: WatchedMoviesViewModel

    @Before
    fun setup() {
        mockkStatic(FirebaseAuth::class)
        mockkStatic(FirebaseFirestore::class)

        val auth = mockk<FirebaseAuth>(relaxed = true)
        every { FirebaseAuth.getInstance() } returns auth
        every { auth.currentUser?.uid } returns "testUid"

        val firestore = mockk<FirebaseFirestore>(relaxed = true)
        every { FirebaseFirestore.getInstance() } returns firestore

        val users = mockk<CollectionReference>(relaxed = true)
        val userDoc = mockk<DocumentReference>(relaxed = true)
        val watched = mockk<CollectionReference>(relaxed = true)
        val watchedDoc = mockk<DocumentReference>(relaxed = true)

        every { firestore.collection("users") } returns users
        every { users.document("testUid") } returns userDoc
        every { userDoc.collection("watched") } returns watched
        every { watched.document(any()) } returns watchedDoc

        viewModel = WatchedMoviesViewModel()
    }

    @Test
    fun `watchedListControl toggles presence`() {
        viewModel.watchedListControl("m1", "/p1")
        assertEquals(1, viewModel.watchedList.getOrAwaitValue().size)

        viewModel.watchedListControl("m1", "/p1")
        assertEquals(0, viewModel.watchedList.getOrAwaitValue().size)
    }
}