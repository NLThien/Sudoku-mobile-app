package com.example.sudokumobileapp.data.local

import androidx.room.Database
import androidx.room.Room
import com.example.sudokumobileapp.data.local.entity.SudokuGameEntity
import androidx.room.RoomDatabase
import com.example.sudokumobileapp.data.local.dao.SudokuGameDao
import android.content.Context
import androidx.room.TypeConverters
import com.example.sudokumobileapp.data.local.converters.SudokuConverters
import com.example.sudokumobileapp.data.local.dao.GameRecordDao
import com.example.sudokumobileapp.data.local.entity.GameRecord

@Database(
    entities = [SudokuGameEntity::class],
    version = 1,
    exportSchema = false // Bỏ qua nếu không cần migration
)
@TypeConverters(SudokuConverters::class) // Thêm converters cho các kiểu phức tạp
abstract class SudokuDatabase : RoomDatabase() {
    abstract fun gameDao(): SudokuGameDao

    companion object {
        @Volatile
        private var instance: SudokuDatabase? = null

        fun getInstance(context: Context): SudokuDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        fun buildDatabase(context: Context): SudokuDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                SudokuDatabase::class.java,
                "sudoku.db"
                )
                    .fallbackToDestructiveMigration()    // Xoá và tạo lại DB nếu version thay đổi
                    .build()
        }
    }
}
@Database(entities = [GameRecord::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameRecordDao(): GameRecordDao
}
