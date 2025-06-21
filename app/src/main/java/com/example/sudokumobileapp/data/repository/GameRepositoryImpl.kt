package com.example.sudokumobileapp.data.repository

// quản lý dữ liệu cục bộ trong ứng dụng(khởi tạo, lưu,

import android.content.Context

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import javax.inject.Inject

//class GameRepositoryImpl @Inject constructor(
//    @ApplicationContext private val context: Context
//) : GameRepository {
//    private val dataStore = context.createDataStore(name = "sudoku_prefs")
//
//    private object PreferencesKeys {
//        val GAME_BOARD = stringPreferencesKey("game_board")
//    }
//
//    override suspend fun saveGame(board: SudokuBoard) {
//        dataStore.edit { preferences ->
//            preferences[PreferencesKeys.GAME_BOARD] = Json.encodeToString(board)
//        }
//    }
//
//    override suspend fun loadGame(): SudokuBoard? {
//        val json = dataStore.data.first()[PreferencesKeys.GAME_BOARD]
//        return json?.let { Json.decodeFromString(it) }
//    }
//}