package pt.isec.amov.tp1

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import pt.isec.amov.tp1.databinding.ActivityMultiplayerLobbyBinding

class MultiplayerLobby : AppCompatActivity() {

    private lateinit var binding : ActivityMultiplayerLobbyBinding

    @SuppressLint("ServiceCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        binding = ActivityMultiplayerLobbyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.copyToClipboardBtn.setOnClickListener(){
            val clip = ClipData.newPlainText("label", binding.serverIpLobby.text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show()
        }

        if(loadImage(this, "avatar.jpg") != null){
            binding.playerAvatarLobby.setImageBitmap(loadImage(this, "avatar.jpg"))
        } else {
            binding.playerAvatarLobby.setImageDrawable(getDrawable(R.drawable.untitled_1))
        }

        if(getUsername(this) != null){
            binding.playerNameLobby.text = getUsername(this)
        } else {
            binding.playerNameLobby.text = R.string.jogador.toString()
        }
    }
}