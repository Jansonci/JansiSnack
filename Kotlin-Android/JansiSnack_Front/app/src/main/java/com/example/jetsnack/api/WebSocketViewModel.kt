package com.example.jetsnack.api
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class WebSocketViewModel : ViewModel() {
    private val messageChannel = Channel<String>(Channel.BUFFERED)
    val messages = messageChannel.receiveAsFlow()  // 用于在 Compose UI 中观察消息
    private val webSocketClient = WebSocketClient()

    init {
        this.webSocketClient.setListener(object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                viewModelScope.launch {
                    messageChannel.send(text)  // 将接收的消息发送到 Channel
                }
            }
        })
        this.webSocketClient.start("ws://10.0.2.2:8080/article/websocket")
    }

    fun sendMessage(message: String) {
        this.webSocketClient.send(message)
    }

    override fun onCleared() {
        super.onCleared()
        this.webSocketClient.close()
    }
}