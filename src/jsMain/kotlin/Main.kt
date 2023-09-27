import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.varabyte.kobweb.silk.SilkStyleSheet
import com.varabyte.kobweb.silk.init.initSilk
import com.varabyte.kobweb.silk.init.setSilkVariables
import de.connect2x.trixnity.messenger.DefaultMatrixClientService
import de.connect2x.trixnity.messenger.trixnityMessengerModule
import de.connect2x.trixnity.messenger.viewmodel.MainViewModel
import de.connect2x.trixnity.messenger.viewmodel.RootRouter
import de.connect2x.trixnity.messenger.viewmodel.RootViewModel
import de.connect2x.trixnity.messenger.viewmodel.RootViewModelImpl
import de.connect2x.trixnity.messenger.viewmodel.connecting.MatrixClientInitializationViewModel
import de.connect2x.trixnity.messenger.viewmodel.util.toFlow
import io.github.oshai.kotlinlogging.KotlinLoggingConfiguration
import io.github.oshai.kotlinlogging.Level
import kotlinx.browser.document
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.css.padding
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable
import org.koin.dsl.koinApplication
import org.w3c.dom.HTMLElement

fun main() {
    KotlinLoggingConfiguration.LOG_LEVEL = Level.DEBUG
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
    // FIXME: This seems to loose type info?
    val routerState by rootViewModel.rootStack.toFlow()
        .mapLatest { it.active.instance }.collectAsState(RootRouter.RootWrapper.None)

    when (routerState) {
        is RootRouter.RootWrapper.None -> {
            None()
        }

        is RootRouter.RootWrapper.MatrixClientInitialization -> {
            Init((routerState as RootRouter.RootWrapper.MatrixClientInitialization).matrixClientInitializationViewModel)
        } // show initialization of the MatrixClient (aka loading screen)

        is RootRouter.RootWrapper.Main -> {
            Main((routerState as RootRouter.RootWrapper.Main).mainViewModel)
        }

        is RootRouter.RootWrapper.AddMatrixAccount -> {
            Login((routerState as RootRouter.RootWrapper.AddMatrixAccount).addMatrixAccountViewModel)
        }

        is RootRouter.RootWrapper.PasswordLogin -> {
            PasswordLogin((routerState as RootRouter.RootWrapper.PasswordLogin).passwordLoginViewModel)
        }

        else -> {} // add more cases
    }
}

@Composable
fun Main(mainViewModel: MainViewModel) {
    Div({ style { padding(25.px) } }) {
        Text("Main")
    }
}

@Composable
fun Init(matrixClientInitializationViewModel: MatrixClientInitializationViewModel) {
    val initState by matrixClientInitializationViewModel.currentState.collectAsState();
    Div({ style { padding(25.px) } }) {
        Text(initState)
    }
}

@Composable
fun None() {
    Div({ style { padding(25.px) } }) {
        Text("Initializing matrix")
    }
}