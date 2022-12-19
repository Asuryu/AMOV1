package pt.isec.amov.tp1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatCallback
import androidx.appcompat.content.res.AppCompatResources
import pt.isec.amov.tp1.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    var isMultiplayer : Boolean = false
    lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.spButton.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("isMultiplayer", isMultiplayer)
            startActivity(intent)
        }

        binding.mpButton.setOnClickListener {
            val intent = Intent(this, MultiplayerLobby::class.java)
            isMultiplayer = true
            intent.putExtra("isMultiplayer", isMultiplayer)
            startActivity(intent)
        }

        binding.top5Button.setOnClickListener {
            val intent = Intent(this, Top5Activity::class.java)
            startActivity(intent)
        }

        binding.editprofileBtn.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

        if(loadImage(this, "avatar.jpg") != null){
            binding.playerAvatar.setImageBitmap(loadImage(this, "avatar.jpg"))
        } else {
            binding.playerAvatar.setImageDrawable(getDrawable(R.drawable.untitled_1))
        }

        if(getUsername(this) != null){
            binding.playerName.text = getUsername(this)
        } else {
            binding.playerName.text = R.string.jogador.toString()
        }
    }

    override fun onResume() {
        super.onResume()
        if(loadImage(this, "avatar.jpg") != null) {
            binding.playerAvatar.setImageBitmap(loadImage(this, "avatar.jpg"))
        }else{
            binding.playerAvatar.setImageDrawable(getDrawable(R.drawable.untitled_1))
        }
        if(getUsername(this) != null){
            binding.playerName.text = getUsername(this)
        } else {
            binding.playerName.text = R.string.jogador.toString()
        }
    }
}
