package com.example.iptvplayer.data

import android.content.Context
import android.net.Uri
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

private val Context.dataStore by preferencesDataStore(name = "iptv_prefs")

/**
 * Loads playlists (from a remote URL or a local file the user picked) and remembers the
 * last playlist URL so the app can reopen it on next launch.
 */
class PlaylistRepository(private val context: Context) {

    private val lastUrlKey = stringPreferencesKey("last_playlist_url")

    suspend fun lastPlaylistUrl(): String? = withContext(Dispatchers.IO) {
        context.dataStore.data.first()[lastUrlKey]
    }

    private suspend fun rememberUrl(url: String) = withContext(Dispatchers.IO) {
        context.dataStore.edit { it[lastUrlKey] = url }
    }

    /** Downloads and parses a playlist from an http(s) URL. */
    suspend fun loadFromUrl(url: String): List<Channel> = withContext(Dispatchers.IO) {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            connectTimeout = 15_000
            readTimeout = 15_000
            instanceFollowRedirects = true
            setRequestProperty("User-Agent", "IPTVPlayer/1.0")
        }
        try {
            val text = connection.inputStream.bufferedReader().use { it.readText() }
            val channels = M3uParser.parse(text)
            if (channels.isNotEmpty()) rememberUrl(url)
            channels
        } finally {
            connection.disconnect()
        }
    }

    /** Reads and parses a playlist from a local content:// or file:// Uri the user picked. */
    suspend fun loadFromUri(uri: Uri): List<Channel> = withContext(Dispatchers.IO) {
        val text = context.contentResolver.openInputStream(uri)
            ?.bufferedReader()
            ?.use { it.readText() }
            ?: error("Could not open the selected file")
        M3uParser.parse(text)
    }
}
