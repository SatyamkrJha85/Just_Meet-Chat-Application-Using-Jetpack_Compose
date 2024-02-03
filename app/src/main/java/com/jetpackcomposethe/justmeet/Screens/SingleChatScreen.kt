package com.jetpackcomposethe.justmeet.Screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jetpackcomposethe.justmeet.CommonDivider
import com.jetpackcomposethe.justmeet.CommonImage
import com.jetpackcomposethe.justmeet.LCViewModel
import com.jetpackcomposethe.justmeet.data.Message


@Composable
fun SingleChatScreen(navController: NavController, vm: LCViewModel, chatId: String) {
    var reply by rememberSaveable {
        mutableStateOf("")
    }

    val myUser = vm.userData.value
    val currentChat = vm.chats.value.first { it.chatId == chatId }
    val chatUser =
        if (myUser?.userId == currentChat.user1.userId) currentChat.user2 else currentChat.user1

    val onSendReply = {
        vm.onSendReply(chatId, reply)
        reply = ""
    }

    var chatMessage = vm.chatMessages

    BackHandler {
        vm.DepopulateMessage()
        navController.popBackStack()
    }

    LaunchedEffect(key1 = Unit) {

        vm.populateMessages(chatId)
    }

    Column {

        ChatHeader(name = chatUser.name ?: "", imageUrl = chatUser.imageUrl ?: "") {
            navController.popBackStack()
            vm.DepopulateMessage()
        }


        MessageBox(modifier = Modifier.weight(1f), chatMessage =chatMessage.value , currentUserId =myUser?.userId ?:"")

        ReplyBox(reply = reply, onReplyChange = { reply = it }, onSendReply = onSendReply)

    }

}


@Composable
fun MessageBox(modifier: Modifier, chatMessage: List<Message>, currentUserId: String) {
    LazyColumn(
        modifier
    ) {
        items(chatMessage) { msg ->
            val alignment = if (msg.sendby == currentUserId) Alignment.End else Alignment.Start
            val color = if (msg.sendby == currentUserId) Color(0xFF68C400) else Color(0xFFC0C0C0)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = alignment
            ) {
                Text(text = msg.message ?: "",
                    modifier= Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(color)
                        .padding(12.dp),color=Color.White, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun ChatHeader(name: String, imageUrl: String, onBackClicker: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = Icons.Rounded.ArrowBack,
            contentDescription = null,
            modifier = Modifier
                .clickable {
                    onBackClicker.invoke()
                }
                .padding(12.dp))

        CommonImage(
            data = imageUrl, modifier = Modifier
                .padding(8.dp)
                .size(50.dp)
                .border(2.dp, Color.DarkGray, shape = CircleShape)
                .clip(shape = CircleShape), contentScale = ContentScale.Crop
        )

        Text(
            text = name, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(start = 5.dp)
        )
    }
}

@Composable
fun ReplyBox(reply: String, onReplyChange: (String) -> Unit, onSendReply: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        CommonDivider()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween

        ) {

            TextField(value = reply, onValueChange = onReplyChange, maxLines = 5)

            Spacer(modifier = Modifier.width(4.dp))
            ElevatedButton(
                modifier = Modifier.clip(shape = CircleShape), onClick = onSendReply
            ) {
                Icon(imageVector = Icons.Rounded.Send, contentDescription = null)
                //   Text(text = "Send")
            }
        }

    }
}