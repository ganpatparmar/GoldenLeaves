package com.skyrider.goldenleaves.activity

import android.os.Bundle
import android.content.Intent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import com.skyrider.goldenleaves.R
import com.skyrider.goldenleaves.ui.theme.GoldenLeavesTheme
import com.skyrider.goldenleaves.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint



@AndroidEntryPoint
class LoginActivity : ComponentActivity() {
    private val viewModel : LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       subscribeToEvents()

        setContent {
            GoldenLeavesTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    LoginScreen()
                }
            }
        }
    }
}

@Composable
fun LoginScreen() {



    var username by remember {
        mutableStateOf(TextFieldValue(""))
    }

    var showProgress: Boolean by remember {
        mutableStateOf(false)
    }
    val rainbowColorsBrush = remember {
        Brush.sweepGradient(
            listOf(
                Color(0xFF9575CD),
                Color(0xFFBA68C8),
                Color(0xFFE57373),
                Color(0xFFFFB74D),
                Color(0xFFFFF176),
                Color(0xFFAED581),
                Color(0xFF4DD0E1),
                Color(0xFF9575CD)
            )
        )
    }
    val c1 = Color(114, 110, 117)


   viewModel.loadingState.observe(this, Observer { uiLoadingState ->
       showProgress = when (uiLoadingState) {
           is LoginViewModel.UiLoadingState.Loading -> {
               true
           }

           is LoginViewModel.UiLoadingState.NotLoading -> {
               false
           }
       }
   })

        Box(){
            Image(
                painter = painterResource(R.drawable.leaf),
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                contentScale = ContentScale.Crop,
                contentDescription = "background")

            Column(verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {



                Text(text = stringResource(R.string.AppTitle),
                    fontWeight = FontWeight.Bold,
                    fontSize = 40.sp,
                    fontStyle = FontStyle.Italic,
                    fontFamily = FontFamily.Serif,
                    color = Color.Green,
                    modifier = Modifier.padding(top = 30.dp)
                )
                Spacer(modifier = Modifier.height(35.dp))

                Image(
                    painter = painterResource(id = R.drawable.goldenleavesappicon),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .height(100.dp)
                        .clip(CircleShape)
                        .width(100.dp)
                        .border(
                            BorderStroke(4.dp, rainbowColorsBrush),
                            CircleShape
                        )

                )

                Spacer(modifier = Modifier.height(40.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { newValue -> username = newValue },
                    label = { Text(text = stringResource(R.string.eUsername),
                        fontWeight = FontWeight.Bold,
                        color = Color.White) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 60.dp, end = 60.dp)




                    ,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )

                Spacer(modifier = Modifier.height(30.dp))




                ExtendedFloatingActionButton(

                    onClick = {

                       viewModel.loginUser(username.text, getString(R.string.jwt_token))
                              },
                    icon = {
                        Icon(
                            Icons.Filled.VerifiedUser,
                            contentDescription = "userlogin"
                        )
                    },
                    text = { Text(stringResource(R.string.LoginButton)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 60.dp, end = 60.dp),
                    backgroundColor = c1
                )
//
                ExtendedFloatingActionButton(
                    onClick = {
                       viewModel.loginUser(username.text)
                              },
                    icon = {
                        Icon(
                            Icons.Filled.Login,
                            contentDescription = "userlogin"
                        )
                    },
                    text = { Text(stringResource(R.string.LoginGuest)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 60.dp, end = 60.dp),
                    backgroundColor = c1
                )

//

                if (showProgress) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(10.dp)

                    )
                }

            }



        }








}

private fun subscribeToEvents() {

    lifecycleScope.launchWhenStarted {

        viewModel.loginEvent.collect { event ->

            when(event) {
                is LoginViewModel.LogInEvent.ErrorInputTooShort -> {
                    showToast("Invalid! Enter more than 3 characters.")
                }

                is LoginViewModel.LogInEvent.ErrorLogIn -> {
                    val errorMessage = event.error
                    showToast("Error: $errorMessage")
                }

                is LoginViewModel.LogInEvent.Success -> {
                    showToast("Login Successful!")
                    startActivity(Intent(this@LoginActivity, ChannelListActivity::class.java))
                    finish()
                }
            }
        }
    }
}

private fun showToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}



@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    GoldenLeavesTheme {
        LoginScreen()


    }
}
