package com.example.iptvplayer.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.iptvplayer.data.Channel
import com.example.iptvplayer.data.PlaylistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UiState(
    val channels: List<Channel> = emptyList(),
    val groups: List<String> = emptyList(),
    val selectedGroup: String? = null,
    val playing: Channel? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
) {
    /** Channels filtered by the currently selected group ("All" when null). */
    val visibleChannels: List<Channel>
        get() = selectedGroup?.let { g -> channels.filter { it.group == g } } ?: channels
}

class IptvViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = PlaylistRepository(app)

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repo.lastPlaylistUrl()?.let { loadUrl(it) }
        }
    }

    fun loadUrl(url: String) {
        val trimmed = url.trim()
        if (trimmed.isEmpty()) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            runCatching { repo.loadFromUrl(trimmed) }
                .onSuccess { publish(it) }
                .onFailure { fail(it, "Could not load the playlist from that URL") }
        }
    }

    fun loadUri(uri: Uri) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            runCatching { repo.loadFromUri(uri) }
                .onSuccess { publish(it) }
                .onFailure { fail(it, "Could not read the selected file") }
        }
    }

    fun selectGroup(group: String?) = _state.update { it.copy(selectedGroup = group) }

    fun play(channel: Channel) = _state.update { it.copy(playing = channel) }

    fun stopPlaying() = _state.update { it.copy(playing = null) }

    fun dismissError() = _state.update { it.copy(error = null) }

    private fun publish(channels: List<Channel>) {
        if (channels.isEmpty()) {
            _state.update {
                it.copy(isLoading = false, error = "No channels found in that playlist")
            }
            return
        }
        val groups = channels.mapNotNull { it.group }.distinct().sorted()
        _state.update {
            it.copy(
                channels = channels,
                groups = groups,
                selectedGroup = null,
                isLoading = false,
                error = null,
            )
        }
    }

    private fun fail(t: Throwable, fallback: String) {
        _state.update {
            it.copy(isLoading = false, error = t.message?.takeIf { m -> m.isNotBlank() } ?: fallback)
        }
    }
}
