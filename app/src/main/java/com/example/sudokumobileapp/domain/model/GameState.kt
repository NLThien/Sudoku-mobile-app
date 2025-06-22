package com.example.sudokumobileapp.domain.model

sealed class GameState {
    object Playing : GameState()
    data class Won(val time: Long) : GameState()
    data class Failed(val wrongCells: List<Pair<Int, Int>>) : GameState()
}

