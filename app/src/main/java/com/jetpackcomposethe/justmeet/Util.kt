package com.jetpackcomposethe.justmeet

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter


@Composable
fun Util(navController: NavController, route: String) {
    navController.navigate(route) {
        popUpTo(route)
        launchSingleTop = true
    }
}

@Composable
fun commonprogressBar() {
    Row(
        modifier = Modifier
            .alpha(0.5f)
            .background(Color.LightGray)
            .fillMaxSize()
            .clickable(enabled = false) { },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()

    }
}

@Composable
fun CheckSignedIn(vm: LCViewModel, navController: NavController) {
    val alreadysignin = remember {
        mutableStateOf(false)
    }

    val signIn = vm.signin.value

    if (signIn && !alreadysignin.value) {
        alreadysignin.value = true
        navController.navigate(DestinationScreen.ChatList.route) {
            popUpTo(0)
        }
    }
}

@Composable
fun mainToast(message: String) {
    val context = LocalContext.current

    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

@Composable
fun CommonDivider() {
    Divider(
        color = Color.LightGray,
        thickness = 2.dp,
        modifier = Modifier
            .alpha(.3f)
            .padding(
                top = 8.dp,
                bottom = 8.dp
            )
    )
}

@Composable
fun CommonImage(
    data: String?, modifier:
    Modifier = Modifier.wrapContentSize(), contentScale:
    ContentScale = ContentScale.Fit
) {
    val painter = rememberImagePainter(data = data)
    Image(
        painter = painter,
        contentDescription = null,
        modifier = modifier,
        contentScale = contentScale
    )
}

@Composable
fun TitleText(txt: String) {
    Text(
        text = txt, fontWeight = FontWeight.Bold, fontSize = 25.sp,
        modifier = Modifier.padding(8.dp)
    )
}

@Composable
fun CommonRow(imageUrl: String?, name: String?, onItemClick: () -> Unit) {
    Row (
        modifier= Modifier
            .fillMaxWidth()
            .height(75.dp)
            .clickable { onItemClick.invoke() },
        verticalAlignment = Alignment.CenterVertically
    ){
        CommonImage(
            data = imageUrl,
            modifier = Modifier
                .padding(8.dp)
                .border(3.dp, Color.DarkGray, shape = CircleShape)
                .size(50.dp)
                .clip(shape = CircleShape)
                .background(Color.Red),
            contentScale = ContentScale.Crop
        )
        Text(text = name ?: "----",
            fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(start = 10.dp))
    }
}