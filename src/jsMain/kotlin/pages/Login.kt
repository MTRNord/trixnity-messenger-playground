import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.forms.InputGroup
import com.varabyte.kobweb.silk.components.forms.TextInput
import de.connect2x.trixnity.messenger.viewmodel.connecting.AddMatrixAccountMethod
import de.connect2x.trixnity.messenger.viewmodel.connecting.AddMatrixAccountViewModel
import de.connect2x.trixnity.messenger.viewmodel.connecting.PasswordLoginViewModel
import kotlinx.coroutines.flow.update
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text


@Composable
fun PasswordLogin(passwordLoginViewModel: PasswordLoginViewModel) {
    val username by passwordLoginViewModel.username.collectAsState();
    val password by passwordLoginViewModel.password.collectAsState();
    val canLogin by passwordLoginViewModel.canLogin.collectAsState();
    Row(
        Modifier
            .display(DisplayStyle.Flex)
            .fillMaxWidth()
            .fillMaxHeight()
            .justifyContent(JustifyContent.Center)
            .alignItems(AlignItems.Center)
    ) {
        Div({
            style {
                display(DisplayStyle.Flex)
                flexDirection(FlexDirection.Column)
                alignItems(AlignItems.FlexStart)
            }
        }) {
            InputGroup(Modifier.display(DisplayStyle.Flex)) {
                LeftAddon(
                    Modifier.display(DisplayStyle.Flex).alignItems(AlignItems.Center)
                ) { Text("Username:") }
                TextInput(
                    username,
                    password = false,
                    onTextChanged = { newText ->
                        passwordLoginViewModel.accountName.update {
                            newText
                        }
                        passwordLoginViewModel.username.update {
                            newText
                        }
                    })
            }
            InputGroup(Modifier.display(DisplayStyle.Flex)) {
                LeftAddon(
                    Modifier.display(DisplayStyle.Flex).alignItems(AlignItems.Center)
                ) { Text("Password:") }
                TextInput(
                    password,
                    password = true,
                    onTextChanged = { newText ->
                        passwordLoginViewModel.password.update {
                            newText
                        }
                    })
            }
            if (canLogin) {
                Button(attrs = {
                    onClick {
                        if (username.isNotBlank() && password.isNotBlank() && canLogin) {
                            passwordLoginViewModel.tryLogin()
                        }
                    }
                }) {
                    Text("Login")
                }
            }
        }
    }
}

@Composable
fun Login(addMatrixAccountViewModel: AddMatrixAccountViewModel) {
    val isFirstMatrixClient by addMatrixAccountViewModel.isFirstMatrixClient.collectAsState();
    val serverUrl by addMatrixAccountViewModel.serverUrl.collectAsState();
    val serverDiscoveryState by addMatrixAccountViewModel.serverDiscoveryState.collectAsState();


    if (isFirstMatrixClient == true) {
        Row(
            Modifier
                .display(DisplayStyle.Flex)
                .fillMaxWidth()
                .fillMaxHeight()
                .justifyContent(JustifyContent.Center)
                .alignItems(AlignItems.Center)
        ) {
            Div({
                style {
                    display(DisplayStyle.Flex)
                    flexDirection(FlexDirection.Column)
                    alignItems(AlignItems.FlexStart)
                }
            }) {
                InputGroup(Modifier.display(DisplayStyle.Flex)) {
                    LeftAddon(Modifier.display(DisplayStyle.Flex).alignItems(AlignItems.Center)) { Text("Homeserver:") }
                    TextInput(
                        serverUrl,
                        password = false,
                        onTextChanged = { newText ->
                            addMatrixAccountViewModel.serverUrl.update {
                                newText
                            }
                        })
                }
                when (serverDiscoveryState) {
                    is AddMatrixAccountViewModel.ServerDiscoveryState.Loading -> Div({
                        classes("lds-dual-ring")
                    })

                    is AddMatrixAccountViewModel.ServerDiscoveryState.Failure -> {
                        Div({ style { color(Color("red")) } }) { Text((serverDiscoveryState as AddMatrixAccountViewModel.ServerDiscoveryState.Failure).message) }
                    }

                    is AddMatrixAccountViewModel.ServerDiscoveryState.Success -> {
                        val availableMethods =
                            (serverDiscoveryState as AddMatrixAccountViewModel.ServerDiscoveryState.Success).addMatrixAccountMethods

                        Div({
                            style {
                                display(DisplayStyle.Flex)
                                flexDirection(FlexDirection.Row)
                                alignItems(AlignItems.Center)
                                justifyContent(JustifyContent.Center)
                            }
                        }) {
                            // FIXME: Make this a tab view instead to change between register and login
                            availableMethods.map { loginMethod ->
                                if (loginMethod is AddMatrixAccountMethod.Password) {
                                    Button(attrs = {
                                        onClick {
                                            addMatrixAccountViewModel.selectAddMatrixAccountMethod(loginMethod)
                                        }
                                    }) {
                                        Text("Login")
                                    }
                                }
                                if (loginMethod is AddMatrixAccountMethod.Register) {
                                    Button(attrs = {
                                        onClick {
                                            addMatrixAccountViewModel.selectAddMatrixAccountMethod(loginMethod)
                                        }
                                    }) {
                                        Text("Register")
                                    }
                                }
                            }
                        }
                    }

                    AddMatrixAccountViewModel.ServerDiscoveryState.None -> {}
                }
            }
        }
        // Render the login
        // 3. Use selectAddMatrixAccountMethod
    } else {
        // Render all accounts + add more
        Div({ style { padding(25.px) } }) {
            Text("not isFirstMatrixClient")
        }
    }
}