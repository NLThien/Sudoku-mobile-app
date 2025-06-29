package com.example.sudokumobileapp.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import android.media.AudioAttributes
import android.media.SoundPool
import com.example.sudokumobileapp.R

object ThemePreferences {
    private val Context.dataStore by preferencesDataStore(name = "settings")
    private val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")

    suspend fun saveTheme(context: Context, isDark: Boolean) {
        context.dataStore.edit { it[DARK_THEME_KEY] = isDark }
    }

    suspend fun loadTheme(context: Context): Boolean {
        val prefs = context.dataStore.data.first()
        return prefs[DARK_THEME_KEY] ?: false // default: light mode
    }
}
object SoundManager {
    private lateinit var soundPool: SoundPool
    private var clickSoundId: Int = 0
    private var numberInputSoundId: Int = 0

    fun init(context: Context) {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()

        clickSoundId = soundPool.load(context, R.raw.click, 1)
        numberInputSoundId = soundPool.load(context, R.raw.input, 1)
    }

    fun playClickSound() {
        if (::soundPool.isInitialized) {
            soundPool.play(clickSoundId, 1f, 1f, 1, 0, 1f)
        }
    }

    fun playNumberInputSound() {
        if (::soundPool.isInitialized) {
            soundPool.play(numberInputSoundId, 1f, 1f, 1, 0, 1f)
        }
    }

    fun release() {
        if (::soundPool.isInitialized) {
            soundPool.release()
        }
    }
}
