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
 * Tests the LOCAL rating logic of RatedMovieViewModel:
 * - add rating
 * - update rating
 * - remove when rating becomes 0
 */
class RatedMovieViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var viewModel: RatedMovieViewModel

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
        val rated = mockk<CollectionReference>(relaxed = true)
        val ratedDoc = mockk<DocumentReference>(relaxed = true)

        every { firestore.collection("users") } returns users
        every { users.document("testUid") } returns userDoc
        every { userDoc.collection("ratedMovies") } returns rated
        every { rated.document(any()) } returns ratedDoc

        viewModel = RatedMovieViewModel()
    }

    @Test
    fun `ratedMoviesControl add update remove`() {
        // add rating 3
        viewModel.ratedMoviesControl("m1", 3)
        val afterAdd = viewModel.ratedMovies.getOrAwaitValue()
        assertEquals(1, afterAdd.size)
        assertEquals(3, afterAdd.first().rating)

        // update to 5
        viewModel.ratedMoviesControl("m1", 5)
        val afterUpdate = viewModel.ratedMovies.getOrAwaitValue()
        assertEquals(1, afterUpdate.size)
        assertEquals(5, afterUpdate.first().rating)

        // remove (set to 0)
        viewModel.ratedMoviesControl("m1", 0)
        val afterRemove = viewModel.ratedMovies.getOrAwaitValue()
        assertEquals(0, afterRemove.size)
    }
}