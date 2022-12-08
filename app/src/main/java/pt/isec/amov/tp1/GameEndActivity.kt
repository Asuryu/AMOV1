package pt.isec.amov.tp1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import pt.isec.amov.tp1.databinding.ActivityGameEndSpBinding

class GameEndActivity : AppCompatActivity() {
    private lateinit var binding : ActivityGameEndSpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameEndSpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val points = intent.getIntExtra("points", 0)
        val level = intent.getIntExtra("level", 0)
        binding.tvPoints.apply {
            text = points.toString()
        }
        binding.tvLevels.apply {
            text = level.toString()
        }

        binding.tryAgain.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }

        binding.mainMenu.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    fun tryAgain(view: View) {
    }

    fun exit(view: View) {

    }
}