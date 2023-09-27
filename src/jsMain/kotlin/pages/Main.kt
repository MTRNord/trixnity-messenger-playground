package pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.varabyte.kobweb.compose.css.borderRight
import de.connect2x.trixnity.messenger.viewmodel.MainViewModel
import de.connect2x.trixnity.messenger.viewmodel.roomlist.RoomListElementViewModel
import de.connect2x.trixnity.messenger.viewmodel.roomlist.RoomListRouter
import de.connect2x.trixnity.messenger.viewmodel.roomlist.RoomListViewModel
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text


@Composable
fun Main(mainViewModel: MainViewModel) {
    mainViewModel.start()
    val roomListData by mainViewModel.roomListRouterStack.subscribeAsState()
    val roomList = roomListData.active.instance


    Div({
        style {
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Row)
            gap(2.px)
            height(100.percent)
        }
    }) {
        Div({
            style {
                height(100.percent)
                width(25.em)
                display(DisplayStyle.Flex)
                flexDirection(FlexDirection.Column)
                gap(2.px)
                borderRight(width = 1.px, style = LineStyle.Solid, color = Color("black"))
                padding(4.px)
            }
        }) {
            if (roomList is RoomListRouter.RoomListWrapper.List) {
                RoomList(roomList.roomListViewModel)
            }
        }
        Div({
            style {
                display(DisplayStyle.Flex)
                flexDirection(FlexDirection.Column)
                gap(2.px)
                height(100.percent)
                width(100.percent)
                padding(4.px)
            }
        }) {
            Text("Room")
        }
    }
}

@Composable
fun RoomList(roomListViewModel: RoomListViewModel) {
    val initialSyncFinished by roomListViewModel.initialSyncFinished.collectAsState();
    val selectedRoomId by roomListViewModel.selectedRoomId.collectAsState();
    val rooms by roomListViewModel.sortedRoomListElementViewModels.collectAsState();

    if (!initialSyncFinished) {
        Div({
            classes("lds-dual-ring")
        })
    } else {
        rooms.forEach { room ->
            Room(room.second, room.first == selectedRoomId)
        }
    }
}

@Composable
fun Room(roomViewModel: RoomListElementViewModel, selected: Boolean) {
    val roomName by roomViewModel.roomName.collectAsState();
    val roomId = roomViewModel.roomId
    Div {
        Text(roomName ?: roomId.full)
    }
}
