package com.example.sudokumobileapp.data.local.converters

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class SudokuConverters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun boardToJson(board: Array<IntArray>): String {
        return json.encodeToString(board)
    }

    @TypeConverter
    fun jsonToBoard(jsonString: String): Array<IntArray> {
        return json.decodeFromString(jsonString)
    }
}