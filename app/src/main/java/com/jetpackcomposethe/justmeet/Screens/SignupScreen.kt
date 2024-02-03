package com.jetpackcomposethe.justmeet.Screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jetpackcomposethe.justmeet.CheckSignedIn
import com.jetpackcomposethe.justmeet.DestinationScreen
import com.jetpackcomposethe.justmeet.LCViewModel
import com.jetpackcomposethe.justmeet.R
import com.jetpackcomposethe.justmeet.commonprogressBar
import com.jetpackcomposethe.justmeet.ui.theme.PurpleGrey40


@Composable
 //@Preview(showSystemUi = true)
//
fun SignupScreen(navController: NavController,vm:LCViewModel) {

    val context = LocalContext.current

    CheckSignedIn(vm = vm, navController =navController )

    Box(modifier = Modifier.fillMaxSize()){
        Column(modifier = Modifier
            .fillMaxSize()
            .fillMaxWidth()
            .wrapContentHeight()
            .verticalScroll(
                rememberScrollState()
            ), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {

            val nameState = remember {
                mutableStateOf(TextFieldValue())
            }

            val numberState = remember {
                mutableStateOf(TextFieldValue())
            }

            val emailState = remember {
                mutableStateOf(TextFieldValue())
            }

            val passwordState = remember {
                mutableStateOf(TextFieldValue())
            }

            val focus = LocalFocusManager.current


            Image(modifier= Modifier
                .size(170.dp)
                .padding(top = 16.dp)
                .padding(8.dp),painter = painterResource(id = R.drawable.signup),
                contentDescription = null)

            Text(text = "Sign Up", fontSize = 30.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.SemiBold,
            modifier=Modifier.padding(8.dp))


            OutlinedTextField(modifier = Modifier
                .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 8.dp)
                .fillMaxWidth(),value = nameState.value,
                onValueChange ={nameState.value=it}
                , label = { Text(text = "Enter Name")},
                leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription =null )},
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                singleLine = true
            )

            OutlinedTextField(modifier = Modifier
                .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 8.dp)
                .fillMaxWidth(),value = numberState.value,
                onValueChange ={numberState.value=it}
                , label = { Text(text = "Enter Number", modifier = Modifier.padding(8.dp))},
                leadingIcon = { Icon(imageVector = Icons.Default.Phone, contentDescription =null )},
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true



            )

            OutlinedTextField(modifier = Modifier
                .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 8.dp)
                .fillMaxWidth(),value = emailState.value,
                onValueChange ={emailState.value=it}
                , label = { Text(text = "Enter Email", modifier = Modifier.padding(8.dp))},
                leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription =null )},
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true



            )

            OutlinedTextField( modifier = Modifier
                .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 8.dp)
                .fillMaxWidth(),value = passwordState.value,
                onValueChange ={passwordState.value=it}
                , label = { Text(text = "Enter Password", modifier = Modifier.padding(8.dp))},
                leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription =null )},
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true



            )

            ElevatedButton(modifier=Modifier.padding(top = 10.dp),
                onClick = {

                    if (nameState.value.text.isEmpty() || nameState.value.text.length < 6 ||
                        numberState.value.text.isEmpty() || numberState.value.text.length < 10 ||
                        emailState.value.text.isEmpty() || emailState.value.text.length < 6 ||
                        passwordState.value.text.isEmpty() || passwordState.value.text.length < 6
                    ) {
                        // Show a toast if any condition is not met
                        Toast.makeText(context,"Please Fill all the Field and value ! < 6 and number ! < 10",Toast.LENGTH_LONG).show()
                    } else {
                        // Call signup function if conditions are met
                        vm.signup(
                            nameState.value.text,
                            numberState.value.text,
                            emailState.value.text,
                            passwordState.value.text,
                        )
                    }
                }) {
                Text(text = "Click To Sign Up")
            }




            TextButton(modifier=Modifier.padding(10.dp),onClick = {
              navController.navigate(DestinationScreen.Login.route)

            }) {
                Text(
                    text = "Already a User ? Go to login -> ",
                    fontWeight = FontWeight.SemiBold, color = Color.Blue

                    )
            }
        }
    }
    if(vm.inProcess.value){
        commonprogressBar()
    }
}