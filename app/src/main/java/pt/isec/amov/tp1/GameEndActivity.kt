package pt.isec.amov.tp1

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.setMargins
import com.google.android.material.imageview.ShapeableImageView
import pt.isec.amov.tp1.databinding.ActivityGameEndMpBinding
import pt.isec.amov.tp1.databinding.ActivityGameEndSpBinding

class GameEndActivity : AppCompatActivity() {
    lateinit var binding : ActivityGameEndMpBinding
    private lateinit var bindingSP : ActivityGameEndSpBinding
    private lateinit var bindingMP : ActivityGameEndMpBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val points = intent.getIntExtra("points", 0)
        val level = intent.getIntExtra("level", 0)
        val isMultiplayer = intent.getBooleanExtra("isMultiplayer", false)
        if (isMultiplayer) {
            bindingMP = ActivityGameEndMpBinding.inflate(layoutInflater)
            setContentView(bindingMP.root)

            val scrollView = bindingMP.scrollView
            val linearLayout = bindingMP.linearScrollView

            val textView = TextView(this)
            textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
            textView.textSize = 30F
            textView.text = "Pontuação"
            textView.setTextColor(getColor(R.color.white))
            textView.setPadding(0, 0, 0, 20)
            linearLayout.addView(textView)

            for(i in 0 until 20){
                val textView = TextView(this)
                textView.text = "Teste $i"
                textView.setTextColor(getColor(R.color.white))
                textView.textSize = 20f
                textView.setPadding(0, 0, 0, 20)
                linearLayout.addView(textView)
            }

            bindingMP.tryAgain.setOnClickListener {
                val intent = Intent(this, GameActivity::class.java)
                intent.putExtra("isMultiplayer", true)
                startActivity(intent)
                finish()
            }
            bindingMP.mainMenu.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        } else {
            bindingSP = ActivityGameEndSpBinding.inflate(layoutInflater)
            setContentView(bindingSP.root)
            bindingSP.tvPoints.text = points.toString()
            bindingSP.tvLevels.text = level.toString()
            bindingSP.tryAgain.setOnClickListener {
                val intent = Intent(this, GameActivity::class.java)
                startActivity(intent)
                finish()
            }
            bindingSP.mainMenu.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}