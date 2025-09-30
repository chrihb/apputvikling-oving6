package ntnu.idi.oving6

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket

class ChatServer(private val PORT: Int) {

    private var clients = mutableListOf<Socket>()

    fun start() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                ServerSocket(PORT).use { socket ->
                    Log.d("ChatServer", "Server started on port $PORT" )
                    while (true) {
                        val client = socket.accept()
                        synchronized(clients) {  clients.add(client) }
                        Log.d("ChatServer", "Client connected: $client")
                        CoroutineScope(Dispatchers.IO).launch { handleClient(client) }
                    }
                }
            } catch (e: Exception) {
               Log.e("ChatServer", "Error starting server: ${e.message}" )
            }
        }
    }

    private fun handleClient(client: Socket) {
        val reader = BufferedReader(InputStreamReader(client.inputStream))
        try {
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                Log.d("ChatServer", "Received: $line")
                broadcastMessage(line!!)
            }
        } finally {
            synchronized(clients) { clients.remove(client) }
            client.close()
        }
    }

    private fun broadcastMessage(message: String?) {
        synchronized(clients) {
            clients.forEach {
                PrintWriter(it.outputStream, true).println(message)
            }
        }
    }
}