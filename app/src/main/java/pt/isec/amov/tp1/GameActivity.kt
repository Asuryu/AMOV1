package pt.isec.amov.tp1

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import pt.isec.amov.tp1.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity() {

    lateinit var binding : ActivityGameBinding

    private lateinit var detector: GestureDetectorCompat
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
           // showEndGameScreen();
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
        detector = GestureDetectorCompat(this,DiaryGestureListenner())
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return if(detector.onTouchEvent(event!!)){
            true
        }else{
            super.onTouchEvent(event)
        }
    }

    inner class DiaryGestureListenner : GestureDetector.SimpleOnGestureListener(){

        private val  SWIPE_THRESHOLD = 100
        private val SWIPE_VELOCITY_THRESHOLD = 100

        override fun onFling(
            downEvent: MotionEvent,
            moveEvent: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            //exclamation to say we acknowledge that the down event will not be null
            var diffX = moveEvent?.x?.minus(downEvent!!.x) ?:0.0F //this means that if this return null, just return this value(0.0) instead
            var diffY = moveEvent?.y?.minus(downEvent!!.y) ?:0.0F //without a 'F' it assumes it's a double
            //we need a float value to make the if else
            return if (Math.abs(diffX) > Math.abs(diffY)) {
                //this is a left or right swipe
               if(Math.abs(diffX)>SWIPE_THRESHOLD && Math.abs(velocityX)>SWIPE_VELOCITY_THRESHOLD){
                    if(diffX>0){
                        //rigth swipe
                        this@GameActivity.onSwipeRight()
                    }else{
                        //left swipe
                        this@GameActivity.onSwipeLeft()
                    }
                    true
                }else{
                    super.onFling(downEvent, moveEvent, velocityX, velocityY)
                }

            }else{
               //this is a top or buttom swipe
               if(Math.abs(diffY)>SWIPE_THRESHOLD && Math.abs(velocityY)>SWIPE_VELOCITY_THRESHOLD){
                    if(diffY>0){
                        this@GameActivity.onSwipeTop()
                    }
                    else{
                        this@GameActivity.onSwipeBottom()
                    }
                   true
                }else{
                    super.onFling(downEvent, moveEvent, velocityX, velocityY)
                }

            }

        }
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




}

   /* override fun onBackPressed() {
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
    }*/

