package pt.isec.amov.tp1

import android.content.ClipData
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pt.isec.amov.tp1.databinding.UsrConnectionBinding

class GetAnotherUserIPActivity : AppCompatActivity() {

    private lateinit var binding : UsrConnectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = UsrConnectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.cpButton2.setOnClickListener {
            val intent = Intent(this, GetAnotherUserIPActivity::class.java)
            startActivity(intent)
        }
    }


}