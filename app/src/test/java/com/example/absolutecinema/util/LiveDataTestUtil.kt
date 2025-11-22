package com.example.absolutecinema.util

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Test helper to synchronously read a LiveData value.
 *
 * Why?
 * - LiveData normally updates asynchronously.
 * - In unit tests we want to "await" the next value then assert on it.
 *
 * How it works:
 * - Subscribes with observeForever
 * - Waits up to [time] for a value using a countdown latch
 * - Removes the observer and returns the value (or throws if none posted)
 */
@VisibleForTesting(otherwise = VisibleForTesting.NONE)
fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS
): T {
    var data: T? = value
    if (data != null) return data as T

    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(t: T) {
            data = t
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }

    observeForever(observer)

    // Wait up to the requested time for LiveData to emit.
    if (!latch.await(time, timeUnit)) {
        removeObserver(observer)
        throw AssertionError("LiveData value was never set.")
    }
    return data as T
}