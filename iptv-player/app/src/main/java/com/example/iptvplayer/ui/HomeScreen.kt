@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.iptvplayer.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.iptvplayer.data.Channel

@Composable
fun HomeScreen(
    state: UiState,
    onLoadUrl: (String) -> Unit,
    onPickFile: () -> Unit,
    onSelectGroup: (String?) -> Unit,
    onPlay: (Channel) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        Header()
        SourceBar(onLoadUrl = onLoadUrl, onPickFile = onPickFile)

        state.error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 8.dp),
            )
        }

        if (state.groups.isNotEmpty()) {
            GroupFilter(
                groups = state.groups,
                selected = state.selectedGroup,
                onSelectGroup = onSelectGroup,
            )
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                state.isLoading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )

                state.channels.isEmpty() -> EmptyHint(
                    modifier = Modifier.align(Alignment.Center)
                )

                else -> ChannelGrid(channels = state.visibleChannels, onPlay = onPlay)
            }
        }
    }
}

@Composable
private fun Header() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 20.dp, bottom = 12.dp),
    ) {
        Icon(
            Icons.Filled.LiveTv,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.width(10.dp))
        Text("IPTV Player", style = MaterialTheme.typography.headlineSmall)
    }
}

@Composable
private fun SourceBar(onLoadUrl: (String) -> Unit, onPickFile: () -> Unit) {
    var url by rememberSaveable { mutableStateOf("") }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
    ) {
        OutlinedTextField(
            value = url,
            onValueChange = { url = it },
            singleLine = true,
            label = { Text("Playlist URL (.m3u / .m3u8)") },
            modifier = Modifier.weight(1f),
        )
        Spacer(Modifier.width(8.dp))
        Button(onClick = { onLoadUrl(url) }) { Text("Load") }
        Spacer(Modifier.width(8.dp))
        OutlinedButton(onClick = onPickFile) {
            Icon(Icons.Filled.FolderOpen, contentDescription = "Open file")
            Spacer(Modifier.width(6.dp))
            Text("File")
        }
    }
}

@Composable
private fun GroupFilter(
    groups: List<String>,
    selected: String?,
    onSelectGroup: (String?) -> Unit,
) {
    val listState = rememberLazyListState()
    androidx.compose.foundation.lazy.LazyRow(
        state = listState,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
    ) {
        item {
            FilterChip(
                selected = selected == null,
                onClick = { onSelectGroup(null) },
                label = { Text("All") },
            )
        }
        groups.forEach { g ->
            item {
                FilterChip(
                    selected = selected == g,
                    onClick = { onSelectGroup(g) },
                    label = { Text(g) },
                )
            }
        }
    }
}

@Composable
private fun ChannelGrid(channels: List<Channel>, onPlay: (Channel) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 180.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 12.dp),
    ) {
        items(channels) { channel ->
            ChannelCard(channel = channel, onPlay = onPlay)
        }
    }
}

@Composable
private fun ChannelCard(channel: Channel, onPlay: (Channel) -> Unit) {
    Card(
        onClick = { onPlay(channel) },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp),
            ),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Icon(
                Icons.Filled.PlayArrow,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            Column {
                Text(
                    text = channel.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                channel.group?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyHint(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.clickable(enabled = false) {},
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            Icons.Filled.LiveTv,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "Load an M3U playlist to see your channels",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        )
    }
}
