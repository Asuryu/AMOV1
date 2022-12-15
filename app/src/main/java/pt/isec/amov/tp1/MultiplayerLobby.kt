package pt.isec.amov.tp1

import android.content.*
import android.graphics.Color
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import pt.isec.amov.tp1.databinding.ActivityMultiplayerLobbyBinding
import android.widget.TextView
import com.google.api.Distribution.BucketOptions.Linear
import org.json.JSONObject
import org.w3c.dom.Text
import java.io.InputStream
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread

class MultiplayerLobby : AppCompatActivity() {

    private lateinit var binding: ActivityMultiplayerLobbyBinding
    lateinit var textView: TextView
    lateinit var button: Button

    private var connectedPlayers = 0
    private var socket: Socket? = null
    private val socketI: InputStream?
        get() = socket?.getInputStream()
    private val socketO: OutputStream?
        get() = socket?.getOutputStream()
    private var serverSocket: ServerSocket? = null
    private var threadComm: Thread? = null

    companion object {
        private const val SERVER_MODE = 0
        private const val CLIENT_MODE = 1
        private const val TAG = "MultiplayerLobby"
        private const val SERVER_PORT = 5000
    }

    private var dlg: AlertDialog? = null

    //  @SuppressLint("ServiceCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMultiplayerLobbyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val ip = intent.getStringExtra("ip")
        if (ip != null) {
            Toast.makeText(this, "IP: $ip", Toast.LENGTH_SHORT).show()
            connectToServer(ip)
        } else {
            startServer()
        }


        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        binding.copyToClipboardBtn.setOnClickListener() {
            val clip = ClipData.newPlainText("label", binding.serverIpLobby.text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show()
        }

        if (loadImage(this, "avatar.jpg") != null) {
            binding.playerAvatarLobby.setImageBitmap(loadImage(this, "avatar.jpg"))
            /*  val drawable = BitmapDrawable(resources, loadImage(this, "avatar.jpg"))
                 binding.playerAvatarIngame.setImageDrawable(drawable)
                 binding.playerAvatarIngame.clipToOutline = true
                 binding.playerAvatarIngame.isEnabled = true*/
        } else {
            binding.playerAvatarLobby.setImageDrawable(getDrawable(R.drawable.untitled_1))
        }

        if (getUsername(this) != null) {
            binding.playerNameLobby.text = getUsername(this)
        } else {
            binding.playerNameLobby.text = R.string.jogador.toString()
        }

        binding.connectToServerBtn.setOnClickListener {
            val edtBox = EditText(this).apply {
                hint = "Server IP"
            }

            val dlg = AlertDialog.Builder(this)
                .setTitle(R.string.client_mode)
                .setMessage(R.string.ask_ip)
                .setPositiveButton(R.string.button_connect) { _: DialogInterface, _: Int ->
                    val strIP = edtBox.text.toString()
                    if (strIP.isEmpty() || !Patterns.IP_ADDRESS.matcher(strIP).matches()) {
                        Toast.makeText(
                            this@MultiplayerLobby,
                            R.string.error_address,
                            Toast.LENGTH_LONG
                        )
                            .show()
                        //finish()
                    } else {
                        // TODO: fechar o nosso servidor
                        val intent = Intent(this, MultiplayerLobby::class.java)
                        intent.putExtra("ip", strIP)
                        startActivity(intent)
                    }
                }

                .setNegativeButton(R.string.button_cancel) { _: DialogInterface, _: Int ->
                    finish()
                }
                .setCancelable(false)
                .setView(edtBox)
                .create()

            dlg?.show()
        }

        binding.startGameBtnLobby.setOnClickListener {
            if(connectedPlayers >= 1) {
                val intent = Intent(this, GameActivity::class.java)
                intent.putExtra("mode", SERVER_MODE)
                startActivity(intent)
            } else {
                Toast.makeText(this, "You need more players to start the game", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun connectToServer(ip: String) {
        var jsonOut = JSONObject()
        jsonOut.put("type", "connect")
        jsonOut.put("name", getUsername(this))
        jsonOut.put("avatar", "avatar.jpg")
        threadComm = thread {
            try {
                socket = Socket(ip, SERVER_PORT)
                Log.d(TAG, "Connected to server")
                socketO?.write(jsonOut.toString().toByteArray())
                socketO?.flush()
            } catch (e: Exception) {
                Log.e(TAG, "Error connecting to server", e)
                finish()
            }
        }
    }

    fun startServer() {
        val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        val ip = wifiManager.connectionInfo.ipAddress
        val strIpAddress = String.format(
            "%d.%d.%d.%d",
            ip and 0xff,
            ip shr 8 and 0xff,
            ip shr 16 and 0xff,
            ip shr 24 and 0xff
        )
        binding.serverIpLobby.text = strIpAddress

        if (serverSocket != null || socket != null) return

        val linearLayout = findViewById<LinearLayout>(binding.connectedPlayersLobby.id)
        linearLayout.removeAllViews()

        thread {
            serverSocket = ServerSocket(SERVER_PORT)
            serverSocket?.run {
                try {
                    while (true) {
                        socket = accept()
                        Log.d(TAG, "Connected to client")
                        connectedPlayers++
                        val jsonIn = JSONObject(socketI?.bufferedReader()?.readLine())
                        val name = jsonIn.getString("name")
                        val avatar = jsonIn.getString("avatar")
                        val type = jsonIn.getString("type")
                        if (type == "connect") {
                            val textView = TextView(this@MultiplayerLobby)
                            textView.text = name
                            textView.textSize = 20F
                            textView.setTextColor(Color.WHITE)
                            linearLayout.addView(textView)
                            val jsonOut = JSONObject()
                            jsonOut.put("type", "connect")
                            jsonOut.put("name", getUsername(this@MultiplayerLobby))
                            jsonOut.put("avatar", "avatar.jpg")
                            socketO?.write(jsonOut.toString().toByteArray())
                            socketO?.flush()
                        }
                        runOnUiThread {
                            addCard(linearLayout, connectedPlayers, getString(R.string.jogadorMp, connectedPlayers))
                        }
                    }
                } catch (_: Exception) {
                    serverSocket?.close()
                    serverSocket = null
                    Intent(this@MultiplayerLobby, MainActivity::class.java).apply {
                        startActivity(this)
                    }
                } finally {
                    serverSocket?.close()
                    serverSocket = null
                }
            }
        }
    }

    private fun addCard(linearLayout: LinearLayout, connectedPlayers: Int, s: String) {
        val playerCard = layoutInflater.inflate(R.layout.top5_card, null)
        val playerNumber = playerCard.findViewById<TextView>(R.id.top_hashtag)
        val playerName = playerCard.findViewById<TextView>(R.id.player_name_top5)
        val playerAvatar = playerCard.findViewById<ImageView>(R.id.player_avatar_top5)
        val playerPoints = playerCard.findViewById<TextView>(R.id.player_points_top5)
        playerPoints.visibility = View.INVISIBLE
        playerNumber.text = "#$connectedPlayers"
        playerName.text = s
        //playerAvatar.setImageBitmap(loadImage(this, s1)) // TODO: Read from json
        linearLayout.addView(playerCard)
    }

    fun startComm(newSocket: Socket) {
        if (threadComm != null)
            return

        socket = newSocket
    }

    fun stopServer() {
        serverSocket?.close()
        serverSocket = null
    }
}

