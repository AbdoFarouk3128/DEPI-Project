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
 * Tests only the LOCAL logic of WatchlistMoviesViewModel:
 * - Adding/removing (toggling) a movie in LiveData
 *
 * We stub Firebase so production code runs without real network/Firebase.
 */
class WatchlistMoviesViewModelTest {

    // Make LiveData post updates synchronously during tests
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: WatchlistMoviesViewModel

    @Before
    fun setup() {
        // Mock static singletons so calls like FirebaseAuth.getInstance() return our stubs
        mockkStatic(FirebaseAuth::class)
        mockkStatic(FirebaseFirestore::class)

        val mockAuth = mockk<FirebaseAuth>(relaxed = true)
        every { FirebaseAuth.getInstance() } returns mockAuth
        every { mockAuth.currentUser?.uid } returns "testUid"

        val mockFirestore = mockk<FirebaseFirestore>(relaxed = true)
        every { FirebaseFirestore.getInstance() } returns mockFirestore

        // Chain the Firestore references used by the VM:
        // users/{uid}/watchlist/{movieId}
        val usersCollection = mockk<CollectionReference>(relaxed = true)
        val userDoc = mockk<DocumentReference>(relaxed = true)
        val watchlistCollection = mockk<CollectionReference>(relaxed = true)
        val watchlistDoc = mockk<DocumentReference>(relaxed = true)

        every { mockFirestore.collection("users") } returns usersCollection
        every { usersCollection.document("testUid") } returns userDoc
        every { userDoc.collection("watchlist") } returns watchlistCollection
        every { watchlistCollection.document(any()) } returns watchlistDoc

        viewModel = WatchlistMoviesViewModel()
    }

    @Test
    fun `watchlistControl adds then removes movie locally`() {
        val id = "123"
        val poster = "/p.jpg"

        // Action: add to watchlist
        viewModel.watchlistControl(id, poster)
        // Assert: LiveData contains the movie
        val afterAdd = viewModel.watchlist.getOrAwaitValue()
        assertEquals(1, afterAdd.size)
        assertEquals(id, afterAdd.first().movieId)

        // Action: toggle again → remove
        viewModel.watchlistControl(id, poster)
        // Assert: LiveData is empty again
        val afterRemove = viewModel.watchlist.getOrAwaitValue()
        assertEquals(0, afterRemove.size)
    }
}