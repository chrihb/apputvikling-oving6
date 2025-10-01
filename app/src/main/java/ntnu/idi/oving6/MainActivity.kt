package ntnu.idi.oving6

import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.security.MessageDigest


class MainActivity : AppCompatActivity() {
    private val PORT = 3000
    private val HOST = "10.0.2.2"
    private lateinit var client: ChatClient
    private lateinit var server: ChatServer
    private lateinit var deviceId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        deviceId = generateTimeHash()
    }


    fun onSendButtonClick(v: View?) {
        val input = findViewById<EditText>(R.id.messageInput).text.toString()
        client.send(deviceId, input)
    }

    fun onStartServerClick(v: View?) {
        val localhost = "127.0.0.1"

        server = ChatServer(PORT)
        server.start()
        client = ChatClient(localhost, PORT) { message ->
            MainScope().launch {
                val chatHistory = findViewById<TextView>(R.id.chatHistory)
                chatHistory.append("\n$message")
            }
        }
        client.start()
    }

    fun onConnectAsClientClick(v: View?) {
        client = ChatClient(HOST, PORT) { message ->
            MainScope().launch {
                val chatHistory = findViewById<TextView>(R.id.chatHistory)
                chatHistory.append("\n$message")
            }
        }
        client.start()
    }

    fun generateTimeHash(): String {
        val now = System.currentTimeMillis().toString()
        val bytes = MessageDigest.getInstance("MD5").digest(now.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }.take(6)
    }
}