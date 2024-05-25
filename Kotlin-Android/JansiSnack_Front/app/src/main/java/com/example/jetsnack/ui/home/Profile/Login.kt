
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetsnack.R
import com.example.jetsnack.api.DessertService
import com.example.jetsnack.api.LoginRequest
import com.example.jetsnack.api.SignUpRequest
import com.example.jetsnack.ui.components.AlertDialog
import com.example.jetsnack.ui.home.Profile.loggedInUser
import com.example.jetsnack.ui.theme.Neutral
import com.example.jetsnack.ui.theme.login
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LoginScreen(onNavigateToRoute: ()->Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    var showDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoggingIn by remember { mutableStateOf(true) }
    var onTo by remember { mutableStateOf("Log in") }

    val focusManager = LocalFocusManager.current

    if(showDialog) {
        ErrorDialog(message = errorMessage) { showDialog = false }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = login) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White)
                        .clip(RoundedCornerShape(4.dp))  // 重复剪裁以应用内部白色背景
                        .padding(8.dp)  // 为文本内容提供内部间距
                        .size(265.dp, 37.dp)
                ) {
                    BasicTextField(
                        value = username,
                        onValueChange = { username = it },
                        textStyle = LocalTextStyle.current.copy(
                            color = Color.Black,
                            fontSize = 16.sp
                        ),
                        cursorBrush = SolidColor(Color.Black),
                        decorationBox = { innerTextField ->
                            if (username.isEmpty()) {
                                Text(
                                    "Username",
                                    style = LocalTextStyle.current.copy(color = Color.Gray)
                                )
                            }
                            innerTextField()
                        },
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .offset(x = 10.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White)
                        .clip(RoundedCornerShape(4.dp))  // 重复剪裁以应用内部白色背景
                        .padding(8.dp)  // 为文本内容提供内部间距
                        .size(265.dp, 37.dp)
                ) {

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        BasicTextField(
                            value = password,
                            onValueChange = { password = it },
                            textStyle = LocalTextStyle.current.copy(
                                color = Color.Black,
                                fontSize = 16.sp
                            ),
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            cursorBrush = SolidColor(Color.Black),
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                            keyboardActions = KeyboardActions.Default,
                            decorationBox = { innerTextField ->
                                if (username.isEmpty()) {
                                    Text(
                                        "Password",
                                        style = LocalTextStyle.current.copy(color = Color.Gray),
                                        modifier = Modifier.offset(x = 3.dp)

                                    )
                                }
                                innerTextField()
                            },
                            modifier = Modifier
                                .offset(x = 10.dp)
                                .weight(1f)
                                .padding(end = 8.dp),
                        )
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = "Toggle password visibility"
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (isLoggingIn) {
                            // 登录逻辑处理
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    val response =
                                        DessertService.create()
                                            .login(LoginRequest(username, password))
                                    Log.i("Response:", response.code().toString())

                                    if (response.isSuccessful) {

                                        withContext(Dispatchers.Main) {
                                            // 主线程中执行导航
                                            loggedInUser =
                                                response.body()?.asJsonObject?.get("userId")?.asLong ?: 0
                                            onNavigateToRoute()
                                        }
                                    } else {
                                        // 登录失败，处理错误状态码和消息
                                        errorMessage = JsonParser.parseString(
                                            response.errorBody()?.string()
                                        ).asJsonObject.get("message")?.asString ?: "6"
                                        withContext(Dispatchers.Main) {
                                            // 处理错误，显示“密码不正确”等具体错误信息
                                            showDialog = true
                                        }
                                    }
                                } catch (e: Exception) {
                                    // 处理连接错误或其他网络问题
                                    withContext(Dispatchers.Main) {
                                        // 显示错误信息，例如无法连接到服务器
                                    }
                                }
                            }
                        }
                        else {
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    val response =
                                        DessertService.create()
                                            .signUp(SignUpRequest(username, password))
                                    if (response.isSuccessful) {
                                        loggedInUser =
                                            response.body()?.asJsonObject?.get("userId")?.asLong
                                                ?: 100
                                        withContext(Dispatchers.Main) {
                                            // 主线程中执行导航
                                            onNavigateToRoute()
                                        }
                                    } else {
                                        // 登录失败，处理错误状态码和消息
                                        errorMessage = JsonParser.parseString(
                                            response.errorBody()?.string()
                                        ).asJsonObject.get("message")?.asString ?: "6"
                                        withContext(Dispatchers.Main) {
                                            // 处理错误，显示“密码不正确”等具体错误信息
                                            showDialog = true
                                        }
                                    }
                                } catch (e: Exception) {
                                    // 处理连接错误或其他网络问题
                                    withContext(Dispatchers.Main) {
                                        // 显示错误信息，例如无法连接到服务器
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),

                    ) {
                    Text(onTo)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (isLoggingIn) {
                        TextButton(onClick = { }) {
                            Text("Forgot Password?")
                        }
                        TextButton(onClick = {
                            focusManager.clearFocus()
                            username = ""
                            password = ""
                            onTo = "Sign up"
                            isLoggingIn = false
                        }) {
                            Text("Sign up")
                        }
                    } else {
                        TextButton(onClick = {
                            focusManager.clearFocus()
                            username = ""
                            password = ""
                            isLoggingIn = true
                            onTo = "Log in"
                        }) {
                            Text("Back")
                        }
                    }
                }
            }
        }
}

@Composable
fun ErrorDialog(message: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Login failed", style = MaterialTheme.typography.bodyLarge,
            fontFamily = FontFamily(Font(R.font.montserrat_semibold))
        ) },
        text = { Text(message) },
        containerColor = Neutral,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Confirm")
            }
        },
        modifier = Modifier.width(50.dp)
    )

}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    LoginScreen {}
}