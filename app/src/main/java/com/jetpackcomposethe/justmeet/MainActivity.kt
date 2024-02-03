package com.jetpackcomposethe.justmeet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jetpackcomposethe.justmeet.Screens.ChatListScreen
import com.jetpackcomposethe.justmeet.Screens.LoginScreen
import com.jetpackcomposethe.justmeet.Screens.ProfileScreen
import com.jetpackcomposethe.justmeet.Screens.SignupScreen
import com.jetpackcomposethe.justmeet.Screens.SingleChatScreen
import com.jetpackcomposethe.justmeet.Screens.SingleStatusScreen
import com.jetpackcomposethe.justmeet.Screens.StatusScreen
import com.jetpackcomposethe.justmeet.ui.theme.JustMeetTheme
import dagger.hilt.android.AndroidEntryPoint

sealed class DestinationScreen(var route:String){
    object SignUp : DestinationScreen("signup")
    object Login : DestinationScreen("login")
    object Profile : DestinationScreen("profile")
    object ChatList : DestinationScreen("chatlist")
    object SingleChat : DestinationScreen("singlechat/{chatId}"){
        fun createRoute(id:String)="singlechat/$id"
    }
    object StatusList : DestinationScreen("statuslist")
    object SingleStatus : DestinationScreen("singlestatus/{userId}"){
        fun createRoute(userId:String)="singlestatus/$userId"
    }

}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JustMeetTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChatAppNavigation()
                }
            }
        }
    }


    @Composable
    fun ChatAppNavigation() {
        val navController= rememberNavController()
        val vm = hiltViewModel<LCViewModel>()
        NavHost(navController = navController, startDestination =  DestinationScreen.SignUp.route){

            composable(DestinationScreen.SignUp.route){
                SignupScreen(navController,vm)
            }

            composable(DestinationScreen.Login.route){
                LoginScreen(navController,vm)
            }
            composable(DestinationScreen.ChatList.route){
                ChatListScreen(navController = navController,vm)
            }

            composable(DestinationScreen.SingleChat.route){
                val chatId=it.arguments?.getString("chatId")
                chatId?.let {
                    SingleChatScreen(navController=navController,vm=vm,chatId=chatId)
                }
            }

            composable(DestinationScreen.StatusList.route){
                StatusScreen(navController = navController,vm)
            }

            composable(DestinationScreen.Profile.route){
                ProfileScreen(navController = navController,vm)
            }

            composable(DestinationScreen.SingleStatus.route){
                val userId =it.arguments?.getString("userId")
                userId?.let {
                    SingleStatusScreen(navController,vm,it)

                }
            }

        }
    }
}


