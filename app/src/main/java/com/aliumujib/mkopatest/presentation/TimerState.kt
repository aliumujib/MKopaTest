package com.aliumujib.mkopatest.presentation

sealed class TimerState {
    data class Running(
        val days: String,
        val hours: String,
        val minutes: String,
        val seconds: String
    ) : TimerState()

    object Loading : TimerState()
    object RunOut : TimerState()
    object Error : TimerState()
}

sealed class TimerEvent {
    object Start : TimerEvent()
}