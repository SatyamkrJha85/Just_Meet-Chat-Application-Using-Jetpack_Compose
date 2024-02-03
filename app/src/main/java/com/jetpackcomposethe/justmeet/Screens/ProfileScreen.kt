package com.jetpackcomposethe.justmeet.Screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jetpackcomposethe.justmeet.CommonDivider
import com.jetpackcomposethe.justmeet.CommonImage
import com.jetpackcomposethe.justmeet.DestinationScreen
import com.jetpackcomposethe.justmeet.LCViewModel
import com.jetpackcomposethe.justmeet.commonprogressBar
import com.jetpackcomposethe.justmeet.data.UserData
import com.jetpackcomposethe.justmeet.mainToast

@Composable
fun ProfileScreen(navController: NavController, vm: LCViewModel) {
    val inProgress = vm.inProcess.value
    val context = LocalContext.current

    if (inProgress) {
        commonprogressBar()
    } else {

        BackHandler {
            navController.popBackStack()
        }

        val userData=vm.userData.value


        var name by rememberSaveable {
         mutableStateOf(userData?.name?:"")
        }

        var number by rememberSaveable {
            mutableStateOf(userData?.number?:"")
        }

        Column (
            modifier=Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally

        ){
            // call the profile content function

            Profilecontent(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp),
                vm = vm,
                name = name,
                number = number,
                onNameChange = {name=it},
                onNumberChange = {number=it},
                onSave = {
                         vm.createorupdateprofile(
                             name=name,number=number
                         )
                 //   vm.inProcess.value=false
                },
                onBack = {
                         navController.navigate(DestinationScreen.ChatList.route){
                             popUpTo(0)
                         }
                },
                onLogout = {
                    vm.logout()
                    Toast.makeText(context,"Log Out Successful", Toast.LENGTH_SHORT).show()

                    navController.navigate(DestinationScreen.Login.route){
                        popUpTo(0)
                    }
                }
            )


            Column(modifier = Modifier.fillMaxWidth()
                ,
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BottomNavMenu(
                    selectedItem = BottomNavigationItem.PROFILELIST,
                    navController = navController
                )
            }

        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Profilecontent(
    vm: LCViewModel,
    modifier: Modifier,
    name: String,
    number: String,
    onNameChange: (String) -> Unit,
    onNumberChange: (String) -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onSave: () -> Unit
) {

    val imageurl = vm.userData.value?.imageurl
    Column(
        modifier=Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,

            ) {
            ElevatedButton(modifier = Modifier
                .height(34.dp)
                .padding(horizontal = 1.dp),
                shape = MaterialTheme.shapes.medium, onClick = {
                    onBack.invoke()
                }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                Text(text = "Back")
            }
//            ElevatedButton(modifier = Modifier
//                .height(34.dp)
//                .padding(horizontal = 1.dp),
//                shape = MaterialTheme.shapes.medium, onClick = {
//                    onSave.invoke()
//                }) {
//                Icon(imageVector = Icons.Default.Check, contentDescription = null)
//               Text(text = "Save")
//            }
        }

      //      CommonDivider()
        Spacer(modifier = Modifier.height(15.dp))

            ProfileImage(imageurl, vm)
        Spacer(modifier = Modifier.height(15.dp))

            // Name Change

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                TextField(
                    value = name, onValueChange = onNameChange, modifier = Modifier,
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Person, contentDescription =number )},
                        colors = TextFieldDefaults
                        .textFieldColors(

                        )
                )
            }

            // Number


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                TextField(
                    value = number, onValueChange = onNumberChange, leadingIcon = {
                      Icon(imageVector = Icons.Default.Phone, contentDescription =number )
                    }, modifier = Modifier
                )
            }

           // CommonDivider()
        Spacer(modifier = Modifier.height(15.dp))
        Spacer(modifier = Modifier.height(15.dp))

            Row (modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 200.dp)
                ,
                horizontalArrangement = Arrangement.Center
            ){
                ElevatedButton(onClick = {
                    onLogout.invoke()
                }) {
                    Text(text = "Logout ->")
                }
            }

    }
}






@Composable
fun ProfileImage(imageurl: String?, vm: LCViewModel) {
    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) {

                uri ->
            uri?.let {
                vm.uploadprofileimage(uri)
            }
        }
    Box(modifier = Modifier.height(intrinsicSize = IntrinsicSize.Min))

    {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .clickable {
                    launcher.launch("image/*")
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                shape = CircleShape, modifier = Modifier
                    .padding(0.dp)
            ) {

                CommonImage(data = imageurl,modifier = Modifier
                    .border(3.dp, Color.DarkGray, shape = CircleShape)
                    .clip(shape = CircleShape)
                    .size(100.dp)
                    .background(Color.Red),
                    contentScale = ContentScale.Crop)
            }
            Text(text = "Change Profile Picture")
        }

        if (vm.inProcess.value) {
            commonprogressBar()
        }
    }
}