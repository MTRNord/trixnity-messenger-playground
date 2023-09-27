package pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.borderRight
import com.varabyte.kobweb.compose.css.overflowX
import com.varabyte.kobweb.compose.css.overflowY
import com.varabyte.kobweb.silk.components.forms.Button
import de.connect2x.trixnity.messenger.viewmodel.MainViewModel
import de.connect2x.trixnity.messenger.viewmodel.room.RoomRouter
import de.connect2x.trixnity.messenger.viewmodel.room.RoomViewModel
import de.connect2x.trixnity.messenger.viewmodel.room.timeline.RoomHeaderViewModel
import de.connect2x.trixnity.messenger.viewmodel.room.timeline.TimelineRouter
import de.connect2x.trixnity.messenger.viewmodel.roomlist.RoomListElementViewModel
import de.connect2x.trixnity.messenger.viewmodel.roomlist.RoomListRouter
import de.connect2x.trixnity.messenger.viewmodel.roomlist.RoomListViewModel
import io.github.oshai.kotlinlogging.KotlinLogging
import net.folivo.trixnity.core.model.RoomId
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

private val log = KotlinLogging.logger { }

@Composable
fun Main(mainViewModel: MainViewModel) {
    suspend {
        mainViewModel.start()
    }
    val roomListData by mainViewModel.roomListRouterStack.subscribeAsState()
    val roomData by mainViewModel.roomRouterStack.subscribeAsState()
    val room = roomData.active.instance
    val roomList = roomListData.active.instance


    Div({
        style {
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Row)
            gap(2.px)
            height(100.vh)
        }
    }) {
        Div({
            style {
                width(25.em)
                display(DisplayStyle.Flex)
                flexDirection(FlexDirection.Column)
                gap(2.px)
                borderRight(width = 1.px, style = LineStyle.Solid, color = Color("black"))
                padding(4.px)
                overflowX(Overflow.Hidden)
                overflowY(Overflow.Auto)
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
                width(100.percent)
                padding(4.px)
            }
        }) {
            when (room) {
                is RoomRouter.RoomWrapper.None -> {
                    Text("Room")
                }

                is RoomRouter.RoomWrapper.View -> {
                    Room(room.roomViewModel)
                }
            }
        }
    }
}

@Composable
fun RoomList(roomListViewModel: RoomListViewModel) {
    val initialSyncFinished by roomListViewModel.initialSyncFinished.collectAsState()
    val selectedRoomId by roomListViewModel.selectedRoomId.collectAsState()
    val rooms by roomListViewModel.sortedRoomListElementViewModels.collectAsState()

    if (!initialSyncFinished) {
        Div({
            classes("lds-dual-ring")
        })
    } else {
        rooms.forEach { room ->
            RoomListItem(room.second, room.first == selectedRoomId) { roomID ->
                roomListViewModel.selectRoom(roomID)
            }
        }
    }
}

@Composable
fun RoomListItem(roomViewModel: RoomListElementViewModel, selected: Boolean, selectRoom: (RoomId) -> Unit) {
    val roomName by roomViewModel.roomName.collectAsState()
    val roomId = roomViewModel.roomId
    val roomNameReal = roomName ?: roomId.full
    Button(onClick = {
        if (!selected) {
            // Geht das eleganter?
            selectRoom(roomId)
        }
    }) {
        Text(roomNameReal)
    }
}

@Composable
fun Room(roomViewModel: RoomViewModel) {
    val timelineStack by roomViewModel.timelineStack.subscribeAsState()
    val timeline = timelineStack.active.instance
    if (timeline is TimelineRouter.TimelineWrapper.View) {
        val roomHeaderViewModel = timeline.timelineViewModel.roomHeaderViewModel

        RoomHeaderElement(roomHeaderViewModel)
    }
}


@Composable
fun RoomHeaderElement(roomHeaderViewModel: RoomHeaderViewModel) {
    val roomHeaderElement by roomHeaderViewModel.roomHeaderElement.collectAsState()
    Text(roomHeaderElement.roomName)
}