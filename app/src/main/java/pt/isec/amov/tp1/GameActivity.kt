package pt.isec.amov.tp1

import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.TextView
import pt.isec.amov.tp1.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity() {

    lateinit var binding : ActivityGameBinding

    companion object {
        private const val TAG = "GameActivity"

        private const val GAME_TIME = 60000L

        private const val BOARD_SIZE = 25
        private const val BOARD_NUMBERS = 9
        private const val BOARD_SIGNS = 12

        var boardNumbers : ArrayList<Int> = ArrayList()
        var boardSigns : ArrayList<String> = ArrayList()
        var level : Int = 1

        fun generateBoard() {
            boardNumbers.clear()
            boardSigns.clear()
            for (i in 0..8) {
                boardNumbers.add((0..9).random())
            }
            for (i in 0..3) {
                boardSigns.add(when ((0..3).random()) {
                    0 -> "+"
                    1 -> "-"
                    2 -> "*"
                    else -> "/"
                })
            }
        }
    }

    private val timer = object: CountDownTimer(GAME_TIME, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            binding.timer?.text = (millisUntilFinished / 1000).toString()
        }

        override fun onFinish() {
            Log.i("Asuryu", "Fim do jogo") // TODO: Quando o jogo acabar, levar para uma nova atividade de final de jogo
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if(loadImage(this, "avatar.jpg") != null){
            val drawable = BitmapDrawable(resources, loadImage(this, "avatar.jpg"))
            binding.playerAvatarIngame.setImageDrawable(drawable)
            binding.playerAvatarIngame.clipToOutline = true
            binding.playerAvatarIngame.isEnabled = true
        } else {
            binding.playerAvatarIngame.setImageDrawable(getDrawable(R.drawable.untitled_1))
        }
        if(getUsername(this) != null){
            binding.playerNameIngame.text = getUsername(this)
        } else {
            binding.playerNameIngame.text = R.string.jogador.toString()
        }
        timer.start()

        var game = Game()
        Log.i("Asuryu", game.board.toString())
        for(i in 0..24){
            val id = resources.getIdentifier("piece$i", "id", packageName)
            val button = findViewById<TextView>(id)
            button.text = game.board[i]
        }

        //TODO: fazer ecrã de créditos (clicar em cima da roda dentada 3 vezes)

    }

    //binding.board.getChildAt(i).setOnClickListener {
    //game.play(i)
    //Log.i("Asuryu", game.board.toString())
    //}

}