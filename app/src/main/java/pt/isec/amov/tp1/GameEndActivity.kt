package pt.isec.amov.tp1

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import pt.isec.amov.tp1.databinding.ActivityGameEndMpBinding
import pt.isec.amov.tp1.databinding.ActivityGameEndSpBinding
import java.io.ByteArrayOutputStream

class GameEndActivity : AppCompatActivity() {
    private lateinit var bindingSP : ActivityGameEndSpBinding
    private lateinit var bindingMP : ActivityGameEndMpBinding

    val storage : FirebaseStorage = FirebaseStorage.getInstance("gs://amov1-gps.appspot.com/")

    val db = Firebase.firestore

    @SuppressLint("WrongThread")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val points = intent.getIntExtra("points", 0)
        val level = intent.getIntExtra("level", 0)
        val totalGameTime = intent.getLongExtra("totalGameTime", 0)
        val isMultiplayer = intent.getBooleanExtra("isMultiplayer", false)

        if(getUsername(this) != null){
            var username = getUsername(this)
            db.collection("top5")
                .whereEqualTo("playerName", username)
                .get()
                .addOnSuccessListener {
                    if(it.isEmpty){
                        db.collection("top5")
                            .add(
                                hashMapOf(
                                    "playerName" to username,
                                    "playerPoints" to points,
                                    "playerTopTime" to totalGameTime,
                                )
                            )
                        // get document id
                        db.collection("top5")
                            .whereEqualTo("playerName", username)
                            .get()
                            .addOnSuccessListener { documents ->
                                for (document in documents) {
                                    if(loadImage(this, "avatar.jpg") != null){
                                        val image = loadImage(this)
                                        val baos = ByteArrayOutputStream()
                                        image?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                                        val data = baos.toByteArray()
                                        val storageRef = storage.reference
                                        val imageRef = storageRef.child("avatars/${document.id}.jpg")
                                        imageRef.putBytes(data)
                                    }
                                }
                            }

                    } else {
                        for(document in it){
                            val currentPoints = document.data["playerPoints"] as Long
                            if(currentPoints < points){
                                db.collection("top5")
                                    .document(document.id)
                                    .update("playerPoints", points)
                            }
                            val currentTopTime = document.data["playerTopTime"] as Long
                            if(currentTopTime < totalGameTime){
                                db.collection("top5")
                                    .document(document.id)
                                    .update("playerTopTime", totalGameTime)
                            }
                            if(loadImage(this, "avatar.jpg") != null){
                                val image = loadImage(this)
                                val baos = ByteArrayOutputStream()
                                image?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                                val data = baos.toByteArray()
                                val storageRef = storage.reference
                                val imageRef = storageRef.child("avatars/${document.id}.jpg")
                                imageRef.putBytes(data)
                            }
                        }
                    }
                }
        }

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

        fun getRandomString(length: Int) : String {
            val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
            return (1..length)
                .map { allowedChars.random() }
                .joinToString("")
        }
}