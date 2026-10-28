package com.example.iptvplayer.data

/**
 * A single playable entry from an M3U playlist.
 *
 * @param name     Display name (from the text after the last comma on the #EXTINF line).
 * @param url      Stream URL (HLS .m3u8, DASH .mpd, or a progressive/TS stream).
 * @param logoUrl  Channel logo, from the `tvg-logo` attribute, if present.
 * @param group    Category, from the `group-title` attribute, if present.
 */
data class Channel(
    val name: String,
    val url: String,
    val logoUrl: String? = null,
    val group: String? = null,
)
