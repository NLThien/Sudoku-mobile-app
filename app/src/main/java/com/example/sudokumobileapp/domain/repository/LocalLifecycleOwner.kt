package com.example.sudokumobileapp.domain.repository

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

class TimerLifecycleObserver(
    private val onPause: () -> Unit,
    private val onResume:()-> Unit

) : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun pause() {
        onPause()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun resume(){
        onResume()
    }
}