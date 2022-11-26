package pt.isec.amov.tp1

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.OnGestureListener
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import pt.isec.amov.tp1.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity() {

    lateinit var binding : ActivityGameBinding

    companion object {
        private const val TAG = "GameActivity"
        var game = Game()
    }

    private val timer = object: CountDownTimer(game.GAME_TIME, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            binding.timer.text = (millisUntilFinished / 1000).toString()
        }

        override fun onFinish() {
            Log.i("Asuryu", "Fim do jogo") // TODO: Quando o jogo acabar, levar para uma nova atividade de final de jogo
            showEndGameScreen();
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
        binding.level.text = getString(R.string.nivel_placeholder, game.level)
        binding.playerPoints.text = getString(R.string.points_placeholder, game.points)

        for(i in 0..24) {
            val id = resources.getIdentifier("piece$i", "id", packageName)
            val button = findViewById<TextView>(id)
            button.text = game.board[i]
        }

        binding.board.getChildAt(1)
    }

    override fun onBackPressed() {
        var builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.exit_dialog_title))
        builder.setMessage(getString(R.string.exit_dialog_message))
        builder.setPositiveButton(getString(R.string.sim)) { _, _ ->
            super.onBackPressed()
        }

        builder.setNegativeButton(getString(R.string.nao)) { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    fun showEndGameScreen(){
        Intent(this, GameEndActivity::class.java).also { startActivity(it) }
        finish()
    }

}