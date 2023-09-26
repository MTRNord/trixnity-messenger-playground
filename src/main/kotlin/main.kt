import androidx.compose.runtime.*
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import de.connect2x.trixnity.messenger.DefaultMatrixClientService
import de.connect2x.trixnity.messenger.trixnityMessengerModule
import de.connect2x.trixnity.messenger.viewmodel.RootViewModel
import de.connect2x.trixnity.messenger.viewmodel.RootViewModelImpl
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable
import org.koin.dsl.koinApplication

fun main() {
    val koinApplication = koinApplication {
        modules(trixnityMessengerModule())
    }
    val matrixClientService = DefaultMatrixClientService(koinApplication.koin)
    val componentContext = DefaultComponentContext(LifecycleRegistry())

    val rootViewModel = RootViewModelImpl(
        componentContext = componentContext,
        matrixClientService = matrixClientService,
        koinApplication = koinApplication,
        initialSyncOnceIsFinished = { finished ->

        }
    )


    renderComposable(rootElementId = "root") {
        Body(rootViewModel)
    }
}

@Composable
fun Body(rootViewModel: RootViewModel) {
    var counter by remember { mutableStateOf(0) }
    Div {
        Text("Clicked: ${counter}")
    }
    Button(
        attrs = {
            onClick { _ ->
                counter++
            }
        }
    ) {
        Text("Click")
    }
}