package com.example.iptvplayer.data

/**
 * Minimal, allocation-light parser for extended M3U playlists (the de-facto IPTV format).
 *
 * Handles entries shaped like:
 * ```
 * #EXTM3U
 * #EXTINF:-1 tvg-logo="http://logo.png" group-title="News",Channel Name
 * http://host/stream.m3u8
 * ```
 *
 * The `#EXTINF` attributes (`tvg-logo`, `group-title`, …) are optional; only a URL line
 * following an `#EXTINF` line is required. Comment and blank lines are ignored.
 */
object M3uParser {

    fun parse(content: String): List<Channel> {
        val channels = mutableListOf<Channel>()

        var pendingName: String? = null
        var pendingLogo: String? = null
        var pendingGroup: String? = null

        content.lineSequence().forEach { raw ->
            val line = raw.trim()
            when {
                line.isEmpty() -> Unit

                line.startsWith("#EXTINF", ignoreCase = true) -> {
                    pendingName = line.substringAfterLast(',', "").trim().ifEmpty { null }
                    pendingLogo = attribute(line, "tvg-logo")
                    pendingGroup = attribute(line, "group-title")
                }

                line.startsWith("#") -> Unit // other directives (#EXTM3U, #EXTVLCOPT, …)

                else -> {
                    // A non-comment line is a stream URL for the preceding #EXTINF (if any).
                    channels += Channel(
                        name = pendingName ?: line.substringAfterLast('/').ifEmpty { line },
                        url = line,
                        logoUrl = pendingLogo,
                        group = pendingGroup,
                    )
                    pendingName = null
                    pendingLogo = null
                    pendingGroup = null
                }
            }
        }
        return channels
    }

    /** Extracts the value of `key="..."` from an #EXTINF line, or null if absent. */
    private fun attribute(line: String, key: String): String? {
        val marker = "$key=\""
        val start = line.indexOf(marker, ignoreCase = true)
        if (start < 0) return null
        val valueStart = start + marker.length
        val end = line.indexOf('"', valueStart)
        if (end < 0) return null
        return line.substring(valueStart, end).trim().ifEmpty { null }
    }
}
