package pt.isec.amov.tp1

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.children
import androidx.gridlayout.widget.GridLayout
import pt.isec.amov.tp1.databinding.ActivityGameBinding

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
            var tmpGame : Game = savedInstanceState.getSerializable("game") as Game
            Log.i("Asuryuu", "Construtor por cópia")
            selectedPieces = savedInstanceState.getSerializable("selectedPieces") as ArrayList<Int>
            for(i in selectedPieces){
                binding.board.getChildAt(i).alpha = 0.2f
            }
            game = Game(tmpGame, this, binding, selectedPieces)
            timeLeft = savedInstanceState.getInt("timeLeft")
            binding.timer.background.setTint(Color.parseColor(game.timerColor[game.level - 1]))

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
            game = Game(this, binding)
            timer = object : CountDownTimer(game.GAME_TIME, 1000) {

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
        }

        binding.level.text = getString(R.string.nivel_placeholder, game.level)
        binding.playerPoints.text = getString(R.string.points_placeholder, game.points)

        // gesture detector TODO: fazer mais detetores

        lateinit var detectorRef : GestureDetectorCompat

        for(i in 0..4){
            for(j in 0..4){
                val id = resources.getIdentifier("piece${i}_${j}", "id", packageName)
                val piece = findViewById<TextView>(id)

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

                if(i % 2 == 0 && j % 2 == 0){
                    piece.setOnTouchListener { v, event ->
                        if (last_move_id != -1)
                            return@setOnTouchListener true

                        val boardId = resources.getIdentifier("board", "id", packageName)
                        val board = findViewById<GridLayout>(boardId)

                        if (detector.onTouchEvent(event)) {
                            var step: Int = 0
                            var cur_id: Int = 0
                            when (last_move_id) {
                                0 -> {
                                    step = 1
                                    cur_id = i * 5 + j
                                }
                                1 -> {
                                    step = -1
                                    cur_id = i * 5 + j
                                }
                                2 -> {
                                    step = 5
                                    cur_id = i * 5 + j
                                }
                                3 -> {
                                    step = -5
                                    cur_id = i * 5 + j
                                }
                            }

                            var selectedExpression: String = ""
                            for (k in 0..4) {
                                val cur_piece = board.getChildAt(cur_id)
                                cur_piece?.alpha = 0.2f;
                                selectedExpression += (cur_piece as TextView).text
                                selectedPieces.add(cur_id)
                                cur_id += step
                            }
                            var ret = game.checkExpression(selectedExpression)
                        }
                        last_move_id = -1
                        true
                    }
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