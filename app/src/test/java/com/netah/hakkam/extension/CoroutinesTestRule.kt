package com.netah.hakkam.numyah.mind.extension

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runners.model.Statement
import kotlin.jvm.Throws

@ExperimentalCoroutinesApi
class CoroutinesTestRule : TestWatcher() {
    val testDispatcher = UnconfinedTestDispatcher()
    val testScope = TestScope(testDispatcher)

    override fun apply(base: Statement, description: Description?): Statement = object : Statement() {
        @Throws(Throwable::class)
        override fun evaluate() {
            Dispatchers.setMain(testDispatcher)
            try {
                base.evaluate()
            } finally {
                Dispatchers.resetMain()
            }
        }
    }
    fun runBlockingTest(block: suspend TestScope.() -> Unit) =
        testScope.runTest { block() }
}
