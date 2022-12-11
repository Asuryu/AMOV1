package pt.isec.ans.rockpaperscissors

import android.net.wifi.WifiManager
import android.text.format.Formatter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.*
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread

class MultiplayerLobbyViewModel : ViewModel() {
    companion object {
        const val SERVER_PORT = 9999
        const val MOVE_NONE = 0
        const val MOVE_ROCK = 1
        const val MOVE_PAPER = 2
        const val MOVE_SCISSORS = 3

        const val NONE = 0
        const val ME = 1
        const val OTHER = 2

    }
    enum class State {
        STARTING, PLAYING_BOTH, PLAYING_ME, PLAYING_OTHER, ROUND_ENDED, GAME_OVER
    }
    enum class ConnectionState {
        SETTING_PARAMETERS, SERVER_CONNECTING, CLIENT_CONNECTING, CONNECTION_ESTABLISHED, CONNECTION_ERROR, CONNECTION_ENDED
    }
    private val _state = MutableLiveData(State.STARTING)
    val state : LiveData<State>
        get() = _state

    private val _connectionState = MutableLiveData(ConnectionState.SETTING_PARAMETERS)
    val connectionState : LiveData<ConnectionState>
        get() = _connectionState

    var myMove = MOVE_NONE
    var otherMove = MOVE_NONE
    var myWins = 0
    var otherWins = 0
    var totalGames = 0
    var lastVictory = NONE


    private var socket: Socket? = null
    private val socketI: InputStream?
        get() = socket?.getInputStream()
    private val socketO: OutputStream?
        get() = socket?.getOutputStream()


    private var threadComm: Thread? = null

    fun startGame() {
        _state.postValue(State.PLAYING_BOTH)
    }
    private fun changeOtherMove(move: Int) {
        if (otherMove != MOVE_NONE || move !in arrayOf(MOVE_ROCK, MOVE_PAPER, MOVE_SCISSORS))
            return
        otherMove = move
        _state.postValue(State.PLAYING_ME)
        checkIfSomeoneWins()
    }

    private fun checkIfSomeoneWins() {
        if (myMove == MOVE_NONE || otherMove == MOVE_NONE)
            return
        val myWin = (myMove == MOVE_ROCK && otherMove == MOVE_SCISSORS) ||
                (myMove == MOVE_PAPER && otherMove == MOVE_ROCK) ||
                (myMove == MOVE_SCISSORS && otherMove == MOVE_PAPER)
        if (myWin) {
            lastVictory = ME
            myWins++
        } else if (myMove != otherMove) {
            lastVictory = OTHER
            otherWins++
        }
        totalGames++
        _state.postValue(State.ROUND_ENDED)
    }
    private fun startComm(newSocket: Socket) {
        if (threadComm != null)
            return

        socket = newSocket

        threadComm = thread {
            try {
                if (socketI == null)
                    return@thread

                _connectionState.postValue(ConnectionState.CONNECTION_ESTABLISHED)
                val bufI = socketI!!.bufferedReader()

                while (_state.value != State.GAME_OVER) {
                    val message = bufI.readLine()
                    val move = message.toIntOrNull() ?: MOVE_NONE
                    changeOtherMove(move)

                }
            } catch (_: Exception) {
            } finally {
                stopGame()
            }
        }
    }

    fun startClient(serverIP: String,serverPort: Int = SERVER_PORT) {
        if (socket != null || _connectionState.value != ConnectionState.SETTING_PARAMETERS)
            return

        thread {
            _connectionState.postValue(ConnectionState.CLIENT_CONNECTING)
            try {
                val newsocket = Socket()
                newsocket.connect(InetSocketAddress(serverIP,serverPort),5000)
                startComm(newsocket)
            } catch (_: Exception) {
                _connectionState.postValue(ConnectionState.CONNECTION_ERROR)
                stopGame()
            }
        }
    }





    fun stopGame() {
        try {
            _state.postValue(State.GAME_OVER)
            _connectionState.postValue(ConnectionState.CONNECTION_ERROR)
            socket?.close()
            socket = null
            threadComm?.interrupt()
            threadComm = null
        } catch (_: Exception) { }
    }
}