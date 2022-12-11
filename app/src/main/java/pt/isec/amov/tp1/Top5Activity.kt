package pt.isec.amov.tp1

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pt.isec.amov.tp1.databinding.ActivityTop5Binding

class Top5Activity : AppCompatActivity() {

    lateinit var binding : ActivityTop5Binding
    val db = Firebase.firestore

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTop5Binding.inflate(layoutInflater)
        setContentView(binding.root)

        getTop5ByPoints()

        binding.byTimeBtn.setOnClickListener{
            getTop5ByTime()
        }
        binding.byPointsBtn.setOnClickListener{
            getTop5ByPoints()
        }

    }

    fun getTop5ByPoints(){
        binding.typeTop5.text = "Points"
        binding.byPointsBtn.isEnabled = false
        binding.byTimeBtn.isEnabled = true
        val linearLayout = findViewById<LinearLayout>(binding.cardWrapperTop5.id)
        linearLayout.removeAllViews()
        db.collection("top5")
            .orderBy("playerPoints", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(5)
            .get()
            .addOnSuccessListener { documents ->
                var count = 1
                for (document in documents){
                    Log.i("Asuryu", "${document.id} => ${document.data}")
                    val card = layoutInflater.inflate(R.layout.top5_card, binding.cardWrapperTop5, false)
                    val hashtag = card.findViewById<TextView>(R.id.top_hashtag)
                    val name = card.findViewById<TextView>(R.id.player_name_top5)
                    val points = card.findViewById<TextView>(R.id.player_points_top5)
                    hashtag.text = "#${count}"
                    count += 1
                    name.text = document.data["playerName"].toString()
                    points.text = getString(R.string.points_placeholder, document.data["playerPoints"] as Long)
                    linearLayout.addView(card)
                }

            }
            .addOnFailureListener { exception ->
                Log.w("Asuryu", "Error getting documents: ", exception)
            }
    }

    fun getTop5ByTime(){
        binding.typeTop5.text = "Time"
        binding.byPointsBtn.isEnabled = true
        binding.byTimeBtn.isEnabled = false
        val linearLayout = findViewById<LinearLayout>(binding.cardWrapperTop5.id)
        linearLayout.removeAllViews()
        db.collection("top5")
            .orderBy("playerTopTime", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(5)
            .get()
            .addOnSuccessListener { documents ->
                var count = 1
                for (document in documents){
                    Log.i("Asuryu", "${document.id} => ${document.data}")
                    val card = layoutInflater.inflate(R.layout.top5_card, binding.cardWrapperTop5, false)
                    val hashtag = card.findViewById<TextView>(R.id.top_hashtag)
                    val name = card.findViewById<TextView>(R.id.player_name_top5)
                    val topTime = card.findViewById<TextView>(R.id.player_points_top5)
                    hashtag.text = "#${count}"
                    count += 1
                    name.text = document.data["playerName"].toString()
                    var time = document.data["playerTopTime"] as Long
                    val seconds = time / 1000
                    topTime.text = "${seconds}s"
                    linearLayout.addView(card)
                }

            }
            .addOnFailureListener { exception ->
                Log.w("Asuryu", "Error getting documents: ", exception)
            }
    }

}