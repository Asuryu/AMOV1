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

            val linearLayout = findViewById<LinearLayout>(bindingMP.container.id)

            for(i in 0 until 20){
                addCard(linearLayout,i, points, level)
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

    private fun addCard(linearLayout: LinearLayout?, i: Int, points: Int, level: Int) {
        val card = layoutInflater.inflate(R.layout.card, null)

        val avatar = card.findViewById<ShapeableImageView>(R.id.player_avatarMp)
        val name = card.findViewById<TextView>(R.id.player_name_2_mp)
        val tvLevel = card.findViewById<TextView>(R.id.player_lvl_2_mp)
        val tvPoints = card.findViewById<TextView>(R.id.player_pts_2_mp)

        //Check is the user has a custom avatar
        if(loadImage(this, "avatar.jpg") != null){
            val drawable = BitmapDrawable(resources, loadImage(this, "avatar.jpg"))
            avatar.setImageDrawable(drawable)
            avatar.clipToOutline = true
            avatar.isEnabled = true
        } else {
            avatar.setImageDrawable(getDrawable(R.drawable.untitled_1))
        }
        if(getUsername(this) != null){
            name.text = getUsername(this)
        } else {
            name.text = getString(R.string.jogadorMp, i)
        }

        val playerLevel = getString(R.string.finalLevelMp, level)
        tvLevel.text = playerLevel
        val playerPoints = getString(R.string.finalPointsMp, points)
        tvPoints.text = playerPoints
        linearLayout?.addView(card)
    }
}