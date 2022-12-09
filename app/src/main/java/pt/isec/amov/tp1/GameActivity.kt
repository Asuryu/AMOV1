package pt.isec.amov.tp1

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.text.TextRunShaper
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
    var isMultiplayer : Boolean = true
    private lateinit var detector : GestureDetectorCompat
    private var last_move_id : Int = -1
    var selectedPieces : ArrayList<Int> = ArrayList()
    var addedPiecesRound: ArrayList<TextView> = ArrayList()

    var timer : CountDownTimer = object : CountDownTimer(10000, 1000) {

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

    public fun showEndGameScreen() {
        val intent = Intent(this, GameEndActivity::class.java)
        intent.putExtra("points", game.points)
        intent.putExtra("level", game.level)
        intent.putExtra("isMultiplayer", isMultiplayer)
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
            var tmpGame : Game = savedInstanceState.getSerializable("game") as Game
            Log.i("Asuryuu", "Construtor por cópia")
            selectedPieces = savedInstanceState.getSerializable("selectedPieces") as ArrayList<Int>
            for(i in selectedPieces){
                binding.board.getChildAt(i).alpha = 0.2f
            }
            game = Game(tmpGame, this, binding, selectedPieces)
            binding.timer.background.setTint(Color.parseColor(game.timerColor[game.level - 1]))
        }
        else {
            game = Game(this, binding)
        }

        // set string with level
        binding.level.text = getString(R.string.nivel_placeholder, game.level)
        binding.playerPoints.text = getString(R.string.points_placeholder, game.points)

        // gesture detector TODO: ASSOCIAR os detetores

        lateinit var detectorRD : GestureDetectorCompat
        lateinit var detectorLD : GestureDetectorCompat
        lateinit var detectorL : GestureDetectorCompat
        lateinit var detectorR : GestureDetectorCompat
        lateinit var detectorRT : GestureDetectorCompat
        lateinit var detectorLT : GestureDetectorCompat
        lateinit var detectorD : GestureDetectorCompat
        lateinit var detectorT : GestureDetectorCompat




        detectorLD = GestureDetectorCompat(this, object : GestureDetector.SimpleOnGestureListener(){
            override fun onFling(
                e1: MotionEvent,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                val diffX = e2!!.x - e1!!.x
                val diffY = e2.y - e1.y
                if(Math.abs(diffX) > Math.abs(diffY)){
                    if(Math.abs(diffX) > 100 && Math.abs(velocityX) > 100){
                        if(diffX > 0){
                            // swipe right
                            super.onFling(e1, e2, velocityX, velocityY)
                        }else{
                            Log.i(TAG, "swipe left")
                            onSwipeLeft()
                            last_move_id = 1
                            return true
                        }
                    }
                } else {
                    if(Math.abs(diffY) > 100 && Math.abs(velocityY) > 100){
                        if(diffY > 0){
                            // swipe down
                            Log.i(TAG, "swipe down")
                            onSwipeBottom()
                            last_move_id = 2
                            return true
                        }else{super.onFling(e1, e2, velocityX, velocityY)}
                    }
                }
                return false
            }
        })

        detectorL = GestureDetectorCompat(this, object : GestureDetector.SimpleOnGestureListener(){
            override fun onFling(
                e1: MotionEvent,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                val diffX = e2!!.x - e1!!.x
                val diffY = e2.y - e1.y
                if(Math.abs(diffX) > Math.abs(diffY)){
                    if(Math.abs(diffX) > 100 && Math.abs(velocityX) > 100){
                        if(diffX > 0){
                            // swipe right
                            super.onFling(e1, e2, velocityX, velocityY)
                        }else{
                            Log.i(TAG, "swipe right")
                            onSwipeLeft()
                            last_move_id = 0
                            return true
                        }
                    }
                } else {
                    if(Math.abs(diffY) > 100 && Math.abs(velocityY) > 100){
                        if(diffY > 0){
                            // swipe down
                            super.onFling(e1, e2, velocityX, velocityY)
                        }else{super.onFling(e1, e2, velocityX, velocityY)}
                    }
                }
                return false
            }
        })

        detectorR = GestureDetectorCompat(this, object : GestureDetector.SimpleOnGestureListener(){
            override fun onFling(
                e1: MotionEvent,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                val diffX = e2!!.x - e1!!.x
                val diffY = e2.y - e1.y
                if(Math.abs(diffX) > Math.abs(diffY)){
                    if(Math.abs(diffX) > 100 && Math.abs(velocityX) > 100){
                        if(diffX > 0){
                            // swipe right
                            Log.i(TAG, "swipe right")
                            onSwipeRight()
                            last_move_id = 0
                            return true
                        }else{
                            super.onFling(e1, e2, velocityX, velocityY)
                        }
                    }
                } else {
                    if(Math.abs(diffY) > 100 && Math.abs(velocityY) > 100){
                        if(diffY > 0){
                            // swipe down
                            super.onFling(e1, e2, velocityX, velocityY)
                        }else{super.onFling(e1, e2, velocityX, velocityY)}
                    }
                }
                return false
            }
        })

        detectorRT = GestureDetectorCompat(this, object : GestureDetector.SimpleOnGestureListener(){
            override fun onFling(
                e1: MotionEvent,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                val diffX = e2!!.x - e1!!.x
                val diffY = e2.y - e1.y
                if(Math.abs(diffX) > Math.abs(diffY)){
                    if(Math.abs(diffX) > 100 && Math.abs(velocityX) > 100){
                        if(diffX > 0){
                            // swipe right
                            Log.i(TAG, "swipe right")
                            onSwipeRight()
                            last_move_id = 0
                            return true
                        }else{
                            super.onFling(e1, e2, velocityX, velocityY)
                        }
                    }
                } else {
                    if(Math.abs(diffY) > 100 && Math.abs(velocityY) > 100){
                        if(diffY > 0){
                            // swipe down
                            super.onFling(e1, e2, velocityX, velocityY)
                        }else{Log.i(TAG, "swipe top")
                            onSwipeTop()
                            last_move_id = 3
                            return true}
                    }
                }
                return false
            }
        })

        detectorLT = GestureDetectorCompat(this, object : GestureDetector.SimpleOnGestureListener(){
            override fun onFling(
                e1: MotionEvent,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                val diffX = e2!!.x - e1!!.x
                val diffY = e2.y - e1.y
                if(Math.abs(diffX) > Math.abs(diffY)){
                    if(Math.abs(diffX) > 100 && Math.abs(velocityX) > 100){
                        if(diffX > 0){
                            // swipe right

                            super.onFling(e1, e2, velocityX, velocityY)
                        }else{
                            Log.i(TAG, "swipe left")
                            onSwipeLeft()
                            last_move_id = 2
                            return true
                        }
                    }
                } else {
                    if(Math.abs(diffY) > 100 && Math.abs(velocityY) > 100){
                        if(diffY > 0){
                            // swipe down
                            super.onFling(e1, e2, velocityX, velocityY)
                        }else{Log.i(TAG, "swipe top")
                            onSwipeTop()
                            last_move_id = 3
                            return true
                            }
                    }
                }
                return false
            }
        })

        detectorT = GestureDetectorCompat(this, object : GestureDetector.SimpleOnGestureListener(){
            override fun onFling(
                e1: MotionEvent,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                val diffX = e2!!.x - e1!!.x
                val diffY = e2.y - e1.y
                if(Math.abs(diffX) > Math.abs(diffY)){
                    if(Math.abs(diffX) > 100 && Math.abs(velocityX) > 100){
                        if(diffX > 0){
                            super.onFling(e1, e2, velocityX, velocityY)
                        }else{
                            super.onFling(e1, e2, velocityX, velocityY)
                        }
                    }
                } else {
                    if(Math.abs(diffY) > 100 && Math.abs(velocityY) > 100){
                        if(diffY > 0){
                            // swipe down
                            super.onFling(e1, e2, velocityX, velocityY)
                        }else{Log.i(TAG, "swipe top")
                            onSwipeTop()
                            last_move_id = 3
                            return true
                            }
                    }
                }
                return false
            }
        })

        detectorD = GestureDetectorCompat(this, object : GestureDetector.SimpleOnGestureListener(){
            override fun onFling(
                e1: MotionEvent,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                val diffX = e2!!.x - e1!!.x
                val diffY = e2.y - e1.y
                if(Math.abs(diffX) > Math.abs(diffY)){
                    if(Math.abs(diffX) > 100 && Math.abs(velocityX) > 100){
                        if(diffX > 0){
                            // swipe right
                            super.onFling(e1, e2, velocityX, velocityY)
                        }else{
                            super.onFling(e1, e2, velocityX, velocityY)
                        }
                    }
                } else {
                    if(Math.abs(diffY) > 100 && Math.abs(velocityY) > 100){
                        if(diffY > 0){
                            // swipe down
                            Log.i(TAG, "swipe down")
                            onSwipeBottom()
                            last_move_id = 2
                            return true
                        }else{super.onFling(e1, e2, velocityX, velocityY)}
                    }
                }
                return false
            }
        })

        for(i in 0..4){
            for(j in 0..4){
                val id = resources.getIdentifier("piece${i}_${j}", "id", packageName)
                val piece = findViewById<TextView>(id)

                lateinit var _detector : GestureDetectorCompat
                var flag = false

                if(i == 0 && j  == 0) {
                    _detector = detectorRD
                    piece.setOnTouchListener { v, event ->
                        if (last_move_id != -1)
                            return@setOnTouchListener true
                        val boardId = resources.getIdentifier("board", "id", packageName)
                        val board = findViewById<GridLayout>(boardId)
                        if (_detector.onTouchEvent(event)) {
                            var step: Int = 0
                            var cur_id: Int = 0
                            Log.i(TAG, "last_move_id = $last_move_id")
                            when (last_move_id) {
                                0 -> {
                                    step = 1
                                    cur_id = i +5
                                }
                                1->{
                                    super.onTouchEvent(event)
                                }
                                2 -> {
                                    step = 5
                                    cur_id = 5 + j
                                }
                                3->{
                                    super.onTouchEvent(event)
                                }
                            }
                            var selectedExpression: String = ""
                            Log.i("Asuryuuu", selectedExpression)
                            }
                        last_move_id = -1
                        true
                }
                }else if(i == 0 && j == 4){
                    _detector = detectorLD
                }else if(i == 4 && j == 0){
                    _detector = detectorRT
                }else if(i == 4 && j == 4){
                    _detector = detectorLT
                }else if(i == 0 && j == 2){
                    _detector = detectorD
                }else if(i == 2 && j == 0){
                    _detector = detectorR
                }else if(i == 2 && j == 4){
                    _detector = detectorL
                }else if(i == 4 && j == 2){
                    _detector = detectorT
                } else flag = true

                if(i % 2 == 0 && j % 2 == 0){
                    piece.setOnTouchListener { v, event ->
                        if (last_move_id != -1)
                            return@setOnTouchListener true

                        val boardId = resources.getIdentifier("board", "id", packageName)
                        val board = findViewById<GridLayout>(boardId)

                        if(flag) return@setOnTouchListener false

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
                                -1 -> return@setOnTouchListener false
                            }

                            addedPiecesRound = ArrayList()
                            var selectedExpression: String = ""
                            for (k in 0..4) {
                                val cur_piece = board.getChildAt(cur_id)
                                cur_piece?.alpha = 0.2f;
                                selectedExpression += (cur_piece as TextView).text
                                selectedPieces.add(cur_id)
                                addedPiecesRound.add(cur_piece)
                                cur_id += step
                            }
                            Log.i("Asuryuuu", selectedExpression)
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
}