package ntnu.idi.oving6

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class ChatClient(private val HOST: String, private val PORT: Int, private val onMessage: (String) -> Unit) {
    private lateinit var socket: Socket

    fun start() {
        CoroutineScope(Dispatchers.IO).launch {
            socket = Socket(HOST, PORT)
            Log.d("ChatClient", "Connected to $HOST:$PORT" )
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
            while (true) {
                val line = reader.readLine() ?: break
                Log.d("ChatClient", "From server $line" )
                onMessage(line)
            }
        }
    }

    fun send(deviceId: String, message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            PrintWriter(socket.getOutputStream(), true).println("$deviceId: $message")
        }
    }
}
