package com.aliumujib.mkopatest.presentation

import app.cash.turbine.test
import com.aliumujib.mkopatest.data.ActiveUsagePeriod
import com.aliumujib.mkopatest.data.ActiveUsagePeriodDataSource
import com.google.common.truth.Truth
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test


@ExperimentalCoroutinesApi
class TimerViewModelTest {

    @MockK
    private lateinit var activeUsagePeriodDataSource: ActiveUsagePeriodDataSource

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun correct_state_is_emitted_when_active_usage_period_is_fetched() =
        runTest {
            launch {
                val mockUsagePeriod = mockk<ActiveUsagePeriod>()
                every {
                    mockUsagePeriod.getSecondsTillExpiry()
                } returns 5
                coEvery {
                    activeUsagePeriodDataSource.getLockingInfo()
                } returns mockUsagePeriod

                val sut = TimerViewModel(activeUsagePeriodDataSource, this)

                sut.uiState
                    .test {
                        sut.produceEvent(TimerEvent.Start)
                        Truth.assertThat(awaitItem()).isEqualTo(TimerState.Loading)
                        Truth.assertThat(awaitItem()).isEqualTo(TimerState.Running("0", "00", "00", "05"))
                        Truth.assertThat(awaitItem()).isEqualTo(TimerState.Running("0", "00", "00", "04"))
                        Truth.assertThat(awaitItem()).isEqualTo(TimerState.Running("0", "00", "00", "03"))
                        Truth.assertThat(awaitItem()).isEqualTo(TimerState.Running("0", "00", "00", "02"))
                        Truth.assertThat(awaitItem()).isEqualTo(TimerState.Running("0", "00", "00", "01"))
                        Truth.assertThat(awaitItem()).isEqualTo(TimerState.Running("0", "00", "00", "00"))
                        Truth.assertThat(awaitItem()).isEqualTo(TimerState.RunOut)
                        awaitComplete()
                    }
            }
        }

    @Test
    fun correct_state_is_emitted_when_active_usage_period_fetch_fails() =
        runTest {
            launch {
                coEvery {
                    activeUsagePeriodDataSource.getLockingInfo()
                } throws Throwable("An error occurred")

                val sut = TimerViewModel(activeUsagePeriodDataSource, this)

                sut.uiState
                    .test {
                        sut.produceEvent(TimerEvent.Start)
                        Truth.assertThat(awaitItem()).isEqualTo(TimerState.Loading)
                        Truth.assertThat(awaitItem()).isEqualTo(TimerState.Error)
                        awaitComplete()
                    }
            }
        }

}