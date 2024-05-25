package com.example.jetsnack.ui.home.Profile

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetsnack.api.DessertService
import com.example.jetsnack.api.UpdateRequest
import com.example.jetsnack.ui.components.JetsnackDivider
import com.example.jetsnack.ui.components.JetsnackSurface
import com.example.jetsnack.ui.theme.AlphaNearOpaque
import com.example.jetsnack.ui.theme.JetnewsTheme
import com.example.jetsnack.ui.theme.JetsnackTheme
import com.example.jetsnack.ui.theme.backGround
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditSpec(upPress:()->Unit, spec:String, specContent:String) {
    val content = remember { mutableStateOf(specContent) }
    val focusManager = LocalFocusManager.current

    Scaffold(topBar = { EditSpecTopBar(upPress, spec, content, focusManager) }) {paddingValues ->
        JetsnackSurface(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            EditSpecContent(spec, content)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditSpecTopBar(upPress:()->Unit, spec: String, content:MutableState<String>, focusManager: FocusManager, ) {
    TopAppBar(
        backgroundColor = JetsnackTheme.colors.uiBackground.copy(alpha = AlphaNearOpaque),
        contentColor = JetsnackTheme.colors.textSecondary,
        contentPadding = WindowInsets.systemBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top).asPaddingValues(),
        elevation = 0.dp,
        modifier = Modifier.height(90.dp)
    )
    {
        val context = LocalContext.current
        val correctedContent = when(spec) {
            "年龄" -> if (content.value.dropLast(1)!= "") {content.value.toInt()} else {0}
            else -> content.value
        }
        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .wrapContentHeight()) {
            Up1(modifier = Modifier
                .weight(1f)
                .offset(y = -12.dp),
                upPress =upPress)
            Text(text = "编辑$spec",style = MaterialTheme.typography.titleMedium, fontSize = 26.sp , modifier = Modifier
                .weight(2f)
                .wrapContentWidth(Alignment.CenterHorizontally)
                .offset(x = 0.dp)
            )
            TextButton(onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            DessertService.create().update(UpdateRequest(loggedInUser, InfoTranslate[spec]?:"", correctedContent))
                            withContext(Dispatchers.Main) {
                                focusManager.clearFocus()
                                Toast.makeText(
                                    context,
                                    "Successfully updated!",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } catch (e: Exception) {
                        // 处理连接错误或其他网络问题
                        withContext(Dispatchers.Main) {
                            Log.e("Update", e.toString())
                            // 显示错误信息，例如无法连接到服务器
                            withContext(Dispatchers.Main) {
                                Log.e("Update", e.toString())
                                // 显示错误信息，例如无法连接到服务器
                                Toast.makeText(
                                    context,
                                    "Server error, try later!",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                        }
                    }
                },modifier = Modifier
                .weight(1f)) {
                Text(text = "确定")
            }
        }
        JetsnackDivider(modifier = Modifier.offset(y=60.dp))
    }
}

@Composable
fun EditSpecContent(spec:String, content:MutableState<String>) {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(color = backGround)
        .padding(10.dp, 20.dp)
        ) {
        when(spec) {
            "性别" -> {
                val selectedGender = remember { mutableStateOf(content.value) }
                Column(modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(28.dp))
                            .border(
                                BorderStroke(
                                    if (selectedGender.value == "男") 2.dp else -4.dp,
                                    Color.DarkGray
                                ),
                                RoundedCornerShape(28.dp)
                            )
                            .background(Color.White)
                            .clip(RoundedCornerShape(28.dp))  // 重复剪裁以应用内部白色背景
                            .padding(8.dp)  // 为文本内容提供内部间距
                            .size(300.dp, 37.dp)
                            .clickable(indication = null,  // 取消涟漪效果
                                interactionSource = remember { MutableInteractionSource() }) {
                                selectedGender.value = "男"
                                content.value = "男"
                            },
                        contentAlignment = Alignment.Center

                    ) {
                        Text(text = "男")
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(28.dp))
                            .border(
                                BorderStroke(
                                    if (selectedGender.value == "女") 2.dp else -4.dp,
                                    Color.DarkGray
                                ),
                                RoundedCornerShape(28.dp)
                            )
                            .background(Color.White)
                            .clip(RoundedCornerShape(28.dp))  // 重复剪裁以应用内部白色背景
                            .padding(8.dp)  // 为文本内容提供内部间距
                            .size(300.dp, 37.dp)
                            .clickable(indication = null,  // 取消涟漪效果
                                interactionSource = remember { MutableInteractionSource() }) {
                                selectedGender.value = "女"
                                content.value = "女"
                            },
                            contentAlignment = Alignment.Center
                    ) {
                        Text(text = "女")
                    }
                }
            }
            else -> {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                        .clip(RoundedCornerShape(8.dp))  // 重复剪裁以应用内部白色背景
                        .padding(8.dp)  // 为文本内容提供内部间距
                        .size(300.dp, 37.dp)
                        .align(Alignment.TopCenter)
                ) {
                    BasicTextField(
                        value = content.value,
                        onValueChange = { content.value = it },
                        textStyle = LocalTextStyle.current.copy(
                            color = Color.Black,
                            fontSize = 16.sp
                        ),
                        cursorBrush = SolidColor(Color.Black),
                        decorationBox = { innerTextField ->
                            if (content.value.isEmpty()) {
                                androidx.compose.material3.Text(
                                    "",
                                    style = LocalTextStyle.current.copy(color = Color.Black),
                                    modifier = Modifier.offset(4.dp, 0.dp)
                                )
                            }
                            innerTextField()
                        },
                        modifier = Modifier
                            .offset(x = 10.dp, y = 6.dp)
                            .background(Color.White)
                            .size(300.dp, 37.dp)
                    )
                }
            }
        }
    }
}

val InfoTranslate = mapOf(Pair("昵称","username"), Pair("性别","gender"), Pair("个性签名","motto"), Pair("年龄","age"),Pair("职业","job"),Pair("所在地","locale"))
@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun EditSpecPreview(){
    JetnewsTheme {
        EditSpec({},"性别", "")
    }
}