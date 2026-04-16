package rachman.forniandi.pretestbni_rachmanforniandi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import rachman.forniandi.pretestbni_rachmanforniandi.presentation.navigation.NavigationGraph
import rachman.forniandi.pretestbni_rachmanforniandi.ui.theme.PretestBni_RachmanForniandiTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PretestBni_RachmanForniandiTheme {
                NavigationGraph()
            }
        }
    }
}


