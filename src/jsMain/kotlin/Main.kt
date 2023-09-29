import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.varabyte.kobweb.silk.SilkStyleSheet
import com.varabyte.kobweb.silk.init.initSilk
import com.varabyte.kobweb.silk.init.setSilkVariables
import de.connect2x.trixnity.messenger.DefaultMatrixClientService
import de.connect2x.trixnity.messenger.trixnityMessengerModule
import de.connect2x.trixnity.messenger.viewmodel.RootRouter
import de.connect2x.trixnity.messenger.viewmodel.RootViewModel
import de.connect2x.trixnity.messenger.viewmodel.RootViewModelImpl
import de.connect2x.trixnity.messenger.viewmodel.connecting.MatrixClientInitializationViewModel
import kotlinx.browser.document
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.css.padding
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable
import org.koin.dsl.koinApplication
import org.w3c.dom.HTMLElement
import pages.Main

fun main() {
    //KotlinLoggingConfiguration.LOG_LEVEL = Level.DEBUG
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
    // Trixnity messenger router
    val routerState by rootViewModel.rootStack.subscribeAsState()
    when (val route = routerState.active.instance) {
        is RootRouter.RootWrapper.MatrixClientInitialization -> {
            Init(route.matrixClientInitializationViewModel)
        } // show initialization of the MatrixClient (aka loading screen)

        is RootRouter.RootWrapper.Main -> {
            Main(route.mainViewModel)
        }

        is RootRouter.RootWrapper.AddMatrixAccount -> {
            Login(route.addMatrixAccountViewModel)
        }

        is RootRouter.RootWrapper.PasswordLogin -> {
            PasswordLogin(route.passwordLoginViewModel)
        }

        else -> {} // add more cases
    }
}


@Composable
fun Init(matrixClientInitializationViewModel: MatrixClientInitializationViewModel) {
    val initState by matrixClientInitializationViewModel.currentState.collectAsState()
    Div({ style { padding(25.px) } }) {
        Text(initState)
    }
}