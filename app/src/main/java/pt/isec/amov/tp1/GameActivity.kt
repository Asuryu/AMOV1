package pt.isec.amov.tp1

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.os.PersistableBundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import androidx.gridlayout.widget.GridLayout
import pt.isec.amov.tp1.databinding.ActivityGameBinding
import java.lang.Math.abs
import kotlin.properties.Delegates

class GameActivity : AppCompatActivity(){

    lateinit var binding : ActivityGameBinding
    lateinit var game : Game
    var timeLeft : Int = 0
    private lateinit var detector : GestureDetectorCompat
    private var last_move_id : Int = -1
    var selectedPieces : ArrayList<Int> = ArrayList()

    var timer : CountDownTimer = object : CountDownTimer(60000, 1000) {

        override fun onTick(millisUntilFinished: Long) {
            binding.timer.text = (millisUntilFinished / 1000).toString()
            timeLeft = ((millisUntilFinished / 1000).toInt())
        }

        override fun onFinish() {
            binding.timer.text = "0"
            timeLeft = 0
            showEndGameScreen()
        }

        fun stopTimer(){
            cancel()
        }
    }

    private fun showEndGameScreen() {
        val intent = Intent(this, GameEndActivity::class.java)
        intent.putExtra("points", game.points)
        intent.putExtra("level", game.level)
        startActivity(intent)
        finish()
    }

    companion object {
        private const val TAG = "GameActivity"
    }


    @SuppressLint("ClickableViewAccessibility")
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

        if(savedInstanceState != null){
            game = savedInstanceState.getSerializable("game") as Game
            timeLeft = savedInstanceState.getInt("timeLeft")
            selectedPieces = savedInstanceState.getSerializable("selectedPieces") as ArrayList<Int>
            for(pieceId in selectedPieces){
                binding.board.getChildAt(pieceId).alpha = 0.2f
            }
            timer = object : CountDownTimer((timeLeft * 1000).toLong(), 1000) {

                override fun onTick(millisUntilFinished: Long) {
                    binding.timer.text = (millisUntilFinished / 1000).toString()
                    timeLeft = ((millisUntilFinished / 1000).toInt())
                }

                override fun onFinish() {
                    binding.timer.text = "0"
                    timeLeft = 0
                    showEndGameScreen()
                }

                fun stopTimer(){
                    cancel()
                }
            }
            timer.start()

        } else {
            game = Game()
            timer.start()
        }

        binding.level.text = getString(R.string.nivel_placeholder, game.level)
        binding.playerPoints.text = getString(R.string.points_placeholder, game.points)
        binding.level.text = getString(R.string.finalLevel, game.level)
        binding.playerPoints.text = getString(R.string.finalPoints, game.points)

        // gesture detector
        detector = GestureDetectorCompat(this, object : GestureDetector.SimpleOnGestureListener(){
            override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
                val diffX = e2!!.x - e1!!.x
                val diffY = e2.y - e1.y
                if(Math.abs(diffX) > Math.abs(diffY)){
                    if(Math.abs(diffX) > 100 && Math.abs(velocityX) > 100){
                        if(diffX > 0){
                            // swipe right
                            Log.i(TAG, "swipe right")
                            onSwipeRight()
                            last_move_id = 0
                        } else {
                            // swipe left
                            Log.i(TAG, "swipe left")
                            onSwipeLeft()
                            last_move_id = 1
                        }
                    }
                } else {
                    if(Math.abs(diffY) > 100 && Math.abs(velocityY) > 100){
                        if(diffY > 0){
                            // swipe down
                            Log.i(TAG, "swipe down")
                            onSwipeBottom()
                            last_move_id = 2
                        } else {
                            // swipe up
                            Log.i(TAG, "swipe up")
                            onSwipeTop()
                            last_move_id = 3
                        }
                    }
                }
                return true
            }
        })

        for(i in 0..4){
            for(j in 0..4){
                val id = resources.getIdentifier("piece${i}_${j}", "id", packageName)
                val piece = findViewById<TextView>(id)
                piece.setOnTouchListener { v, event ->
                    if (last_move_id != -1)
                        return@setOnTouchListener true

                    val boardId = resources.getIdentifier("board", "id", packageName)
                    val board = findViewById<GridLayout>(boardId)
                    if(detector.onTouchEvent(event)){
                        Log.i("Asuryu", "fixe" + last_move_id)
                        var step : Int = 0
                        var cur_id : Int = 0
                        when(last_move_id){
                            0 -> { step = 1; cur_id = i * 5; }
                            1 -> { step = -1; cur_id = i * 5; }
                            2 -> { step = 5; cur_id = j; }
                            3 -> { step = -5;  cur_id = j;}
                        }

                        for (k in 0..4)
                        {
                            Log.i("Asuryu", "Cur ID: " + cur_id)
                            val cur_piece = board.getChildAt(cur_id)
                            cur_piece?.alpha = 0.2f;
                            selectedPieces.add(cur_id)
                            cur_id += step
                        }
                    }

                    last_move_id = -1
                    true
                }
                piece.text = game.board[i][j]
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("game", game)
        outState.putInt("timeLeft", timeLeft)
        outState.putSerializable("selectedPieces", selectedPieces)
    }

    private fun onSwipeBottom() {
        Toast.makeText(this, "Bottom Swipe", Toast.LENGTH_LONG).show()
    }

    private fun onSwipeTop() {
        Toast.makeText(this, "Top Swipe", Toast.LENGTH_LONG).show()
    }

    private fun onSwipeLeft() {
        Toast.makeText(this, "Left Swipe", Toast.LENGTH_LONG).show()
    }

    private fun onSwipeRight() {
        Toast.makeText(this, "Right Swipe", Toast.LENGTH_LONG).show()
    }


    override fun onBackPressed() {
        var builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.exit_dialog_title))
        builder.setMessage(getString(R.string.exit_dialog_message))
        builder.setPositiveButton(getString(R.string.sim)) { _, _ ->
            super.onBackPressed()
            timer.cancel()
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