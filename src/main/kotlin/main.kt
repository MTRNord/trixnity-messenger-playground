import androidx.compose.runtime.*
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.varabyte.kobweb.silk.SilkStyleSheet
import com.varabyte.kobweb.silk.init.initSilk
import com.varabyte.kobweb.silk.init.setSilkVariables
import de.connect2x.trixnity.messenger.DefaultMatrixClientService
import de.connect2x.trixnity.messenger.trixnityMessengerModule
import de.connect2x.trixnity.messenger.viewmodel.RootRouter
import de.connect2x.trixnity.messenger.viewmodel.RootViewModel
import de.connect2x.trixnity.messenger.viewmodel.RootViewModelImpl
import de.connect2x.trixnity.messenger.viewmodel.util.toFlow
import kotlinx.browser.document
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.renderComposable
import org.koin.dsl.koinApplication
import org.w3c.dom.HTMLElement

fun main() {
    // Init Kobweb
    initSilk()

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
        // Further Init for kobweb
        val root = remember { document.getElementById("root") as HTMLElement }
        root.setSilkVariables()
        Style(SilkStyleSheet)

        Base(rootViewModel)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun Base(rootViewModel: RootViewModel) {
    suspend {
        rootViewModel.rootStack.toFlow()
            .mapLatest { it.active.instance }
            .collect { wrapper ->
                when(wrapper) {
                    is RootRouter.RootWrapper.None -> {

                    } // draw an empty UI
                    is RootRouter.RootWrapper.MatrixClientInitialization -> {} // show initialization of the MatrixClient (aka loading screen)
                    else -> {} // add more cases
                }
            }
    }
}