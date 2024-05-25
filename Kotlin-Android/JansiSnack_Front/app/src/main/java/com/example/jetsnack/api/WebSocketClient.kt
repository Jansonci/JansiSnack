package com.example.jetsnack.api

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class WebSocketClient {
    private var client: OkHttpClient? = null
    private var webSocket: WebSocket? = null
    private var listener: WebSocketListener? = null  // 储存外部设置的监听器

    fun setListener(customListener: WebSocketListener) {
        this.listener = customListener  // 设置外部传入的监听器
    }

    fun start(url: String) {
        client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        listener = listener ?: object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                // 默认的 WebSocket 连接打开时的操作
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.i("onMessage", text)
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                webSocket.close(1000, null)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                // 默认的处理连接失败的情况
            }
        }
        webSocket = client!!.newWebSocket(request, listener!!)
    }

    fun send(message: String) {
        webSocket?.send(message)
    }

    fun close() {
        webSocket?.close(1000, "Closing Connection")
    }
}