package pt.isec.amov.tp1

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AlertDialog
import pt.isec.amov.tp1.databinding.ActivityMultiplayerLobbyBinding
import android.widget.TextView

class MultiplayerLobby : AppCompatActivity() {

    private lateinit var binding: ActivityMultiplayerLobbyBinding
    lateinit var textView: TextView
    lateinit var button: Button

    companion object {
        private const val SERVER_MODE = 0
        private const val CLIENT_MODE = 1
        private const val TAG = "MultiplayerLobby"
    }

    private var dlg: AlertDialog? = null

    //  @SuppressLint("ServiceCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMultiplayerLobbyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val ip = intent.getStringExtra("ip")
        if (ip != null) {
            Toast.makeText(this, "IP: $ip", Toast.LENGTH_SHORT).show()
        }


        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        binding.copyToClipboardBtn.setOnClickListener() {
            val clip = ClipData.newPlainText("label", binding.serverIpLobby.text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show()
        }

        if (loadImage(this, "avatar.jpg") != null) {
            binding.playerAvatarLobby.setImageBitmap(loadImage(this, "avatar.jpg"))
            /*  val drawable = BitmapDrawable(resources, loadImage(this, "avatar.jpg"))
                 binding.playerAvatarIngame.setImageDrawable(drawable)
                 binding.playerAvatarIngame.clipToOutline = true
                 binding.playerAvatarIngame.isEnabled = true*/
        } else {
            binding.playerAvatarLobby.setImageDrawable(getDrawable(R.drawable.untitled_1))
        }

        if (getUsername(this) != null) {
            binding.playerNameLobby.text = getUsername(this)
        } else {
            binding.playerNameLobby.text = R.string.jogador.toString()
        }

        binding.connectToServerBtn.setOnClickListener {
            val edtBox = EditText(this).apply {
                hint = "Server IP"
            }

            val dlg = AlertDialog.Builder(this)
                .setTitle(R.string.client_mode)
                .setMessage(R.string.ask_ip)
                .setPositiveButton(R.string.button_connect) { _: DialogInterface, _: Int ->
                    val strIP = edtBox.text.toString()
                    if (strIP.isEmpty() || !Patterns.IP_ADDRESS.matcher(strIP).matches()) {
                        Toast.makeText(
                            this@MultiplayerLobby,
                            R.string.error_address,
                            Toast.LENGTH_LONG
                        )
                            .show()
                        //finish()
                    } else {
                        // TODO: fechar o nosso servidor
                        val intent = Intent(this, MultiplayerLobby::class.java)
                        intent.putExtra("ip", strIP)
                        startActivity(intent)
                    }
                }

                .setNegativeButton(R.string.button_cancel) { _: DialogInterface, _: Int ->
                    finish()
                }
                .setCancelable(false)
                .setView(edtBox)
                .create()

            dlg?.show()
        }
    }
}