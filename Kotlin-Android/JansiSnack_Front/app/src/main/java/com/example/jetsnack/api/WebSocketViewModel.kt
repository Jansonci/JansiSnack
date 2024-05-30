package com.example.jetsnack.api
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class WebSocketViewModel : ViewModel() {
    private val messageChannel = Channel<String>(Channel.BUFFERED)
    val messages = messageChannel.receiveAsFlow()  // 用于在 Compose UI 中观察消息
    private val webSocketClient = WebSocketClient()

    init {
        connectWebSocket()
        Log.i("yyy", "WebSocketViewModel Created")
    }

    private fun connectWebSocket() {
        viewModelScope.launch {
            try {
                this@WebSocketViewModel.webSocketClient.setListener(object : WebSocketListener() {
                    override fun onMessage(webSocket: WebSocket, text: String) {
                        viewModelScope.launch {
                            messageChannel.send(text)  // 将接收的消息发送到 Channel
                        }
                    }

                    override fun onFailure(
                        webSocket: WebSocket,
                        t: Throwable,
                        response: Response?
                    ) {
                        // 处理连接失败
                        viewModelScope.launch {
                            messageChannel.send("Connection failed: ${t.message}")
                            retryConnection()
                        }
                        // 重试连接
                    }
                })
                this@WebSocketViewModel.webSocketClient.start("ws://10.0.2.2:8080/article/websocket")
            } catch (e: Exception) {
                Log.e("WebSocketError", "Failed to connect: ${e.message}")
//                messageChannel.send("Failed to connect: ${e.message}")
                retryConnection()
            }
        }
    }

    private suspend fun retryConnection() {
        delay(5000)  // 延迟5秒后重试连接
        connectWebSocket()
    }

    fun sendMessage(message: String) {
        this.webSocketClient.send(message)
        Log.i("yyysending", message)
    }

    override fun onCleared() {
        super.onCleared()
        this.webSocketClient.close()
        Log.i("yyy", "WebSocketViewModel Destroyed")
    }
}