package pt.isec.amov.tp1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import pt.isec.amov.tp1.databinding.ActivityMultiplayerLobbyBinding

class MultiplayerLobby : AppCompatActivity() {

    private lateinit var binding : ActivityMultiplayerLobbyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMultiplayerLobbyBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}