package com.netah.hakkam.numyah.mind.extension

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.junit.Assert

class TestObserver<T>(
    scope: CoroutineScope,
    flow: Flow<T>
) {
    private val values = mutableListOf<T>()
    private val job: Job = scope.launch {
        flow.collect { data -> values.add(data) }
    }

    fun assertNoValues(): TestObserver<T> {
        Assert.assertEquals(emptyList<T>(), this.values)
        return this
    }

    fun assertValues(vararg values: T): TestObserver<T> {
        Assert.assertEquals(values.toList(), this.values)
        return this
    }

    fun finish() {
        job.cancel()
    }
}
