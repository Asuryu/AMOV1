package pt.isec.amov.tp1

import android.annotation.SuppressLint
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import pt.isec.amov.tp1.databinding.ActivityCreditsBinding

class CreditsActivity : AppCompatActivity() {
    lateinit var binding : ActivityCreditsBinding

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreditsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val avatarTomas = getDrawable(R.drawable.tomas)
        if (avatarTomas != null) {
            binding.playerAvatar0.setImageDrawable(avatarTomas)
            binding.playerAvatar0.clipToOutline = true
            binding.playerAvatar0.isEnabled = true
        }else{
            binding.playerAvatar0.setImageDrawable(getDrawable(R.drawable.untitled_1))
        }

        val avatarTania = getDrawable(R.drawable.tania)
        if (avatarTania != null) {
            binding.playerAvatar1.setImageDrawable(avatarTania)
            binding.playerAvatar1.clipToOutline = true
            binding.playerAvatar1.isEnabled = true
        }else{
            binding.playerAvatar1.setImageDrawable(getDrawable(R.drawable.untitled_1))
        }

        val avatarRafael = getDrawable(R.drawable.rafael)
        if (avatarRafael != null) {
            binding.playerAvatar2.setImageDrawable(avatarRafael)
            binding.playerAvatar2.clipToOutline = true
            binding.playerAvatar2.isEnabled = true
        }else{
            binding.playerAvatar2.setImageDrawable(getDrawable(R.drawable.untitled_1))
        }
    }
}