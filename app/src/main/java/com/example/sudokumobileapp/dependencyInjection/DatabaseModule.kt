package com.example.sudokumobileapp.dependencyInjection

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import javax.inject.Singleton
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import com.example.sudokumobileapp.data.local.dao.SudokuGameDao
import com.example.sudokumobileapp.data.local.SudokuDatabase
import android.content.Context

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SudokuDatabase {
        return SudokuDatabase.getInstance(context)
    }

    @Provides
    fun provideGameDao(database: SudokuDatabase): SudokuGameDao {
        return database.gameDao()
    }
}