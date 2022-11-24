package pt.isec.amov.tp1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import pt.isec.amov.tp1.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity() {

    lateinit var binding : ActivityGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}