package com.aliumujib.mkopatest.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aliumujib.mkopatest.data.ActiveUsagePeriodDataSource
import com.aliumujib.mkopatest.data.ActiveUsagePeriodDataSourceImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.takeWhile

@OptIn(ExperimentalCoroutinesApi::class)
class TimerViewModel(
    private val dataSource: ActiveUsagePeriodDataSource = ActiveUsagePeriodDataSourceImpl(),
    scope: CoroutineScope? = null
) : ViewModel() {

    private val eventChannel = Channel<TimerEvent>(Channel.BUFFERED)

    val produceEvent = { event: TimerEvent ->
        if (!eventChannel.isClosedForSend) {
            eventChannel.trySend(event)
        }
    }

    val uiState: StateFlow<TimerState> =
        eventChannel.consumeAsFlow()
            .flatMapMerge {
                runTimer()
            }.takeWhile { it !is TimerState.RunOut || it !is TimerState.Error }
            .stateIn(
                scope = scope ?: viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = TimerState.Loading
            )

    private fun runTimer(): Flow<TimerState> {
        return flow {
            emit(dataSource.getLockingInfo().getSecondsTillExpiry())
        }.flatMapMerge { secondsTillExpiry ->
            generateSequence(secondsTillExpiry) { it - 1 }.asFlow()
        }.takeWhile { it >= 0 }
            .onEach {
                delay(1000)
            }.map { timeLeft ->
                val dd = (timeLeft / 86400).toString()
                val hh = (timeLeft / 3600).toString().padStart(2, '0')
                val mm = ((timeLeft / 60) % 60).toString().padStart(2, '0')
                val ss = (timeLeft % 60).toString().padStart(2, '0')
                TimerState.Running(dd, hh, mm, ss) as TimerState
            }.onCompletion {
                emit(TimerState.RunOut)
            }.catch {
                emit(TimerState.Error)
            }
    }

}
