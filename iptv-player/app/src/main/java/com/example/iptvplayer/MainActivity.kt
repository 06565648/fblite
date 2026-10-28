package com.example.iptvplayer

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.iptvplayer.player.PlayerScreen
import com.example.iptvplayer.ui.HomeScreen
import com.example.iptvplayer.ui.IptvViewModel
import com.example.iptvplayer.ui.theme.IptvTheme

class MainActivity : ComponentActivity() {

    private val viewModel: IptvViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Don't dim/sleep the screen while watching.
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val pickFile = registerForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) { uri -> uri?.let(viewModel::loadUri) }

        setContent {
            IptvTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val state by viewModel.state.collectAsStateWithLifecycle()

                    val playing = state.playing
                    if (playing != null) {
                        PlayerScreen(channel = playing, onExit = viewModel::stopPlaying)
                    } else {
                        HomeScreen(
                            state = state,
                            onLoadUrl = viewModel::loadUrl,
                            onPickFile = {
                                pickFile.launch(
                                    arrayOf("audio/x-mpegurl", "application/x-mpegurl", "*/*")
                                )
                            },
                            onSelectGroup = viewModel::selectGroup,
                            onPlay = viewModel::play,
                        )
                    }
                }
            }
        }
    }
}
