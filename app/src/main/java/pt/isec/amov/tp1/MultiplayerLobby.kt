package pt.isec.amov.tp1

import android.annotation.SuppressLint
import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.view.postDelayed
import pt.isec.amov.tp1.databinding.ActivityMultiplayerLobbyBinding
import pt.isec.ans.rockpaperscissors.MultiplayerLobbyViewModel

class MultiplayerLobby : AppCompatActivity() {

    private lateinit var binding : ActivityMultiplayerLobbyBinding
    companion object {
        private const val SERVER_MODE = 0
        private const val CLIENT_MODE = 1


        fun getClientModeIntent(context : Context) : Intent {
            return Intent(context,ActivityMultiplayerLobbyBinding::class.java).apply {
                putExtra("mode", CLIENT_MODE)
            }
        }
    }
    private val model: MultiplayerLobbyViewModel by this.viewModels()


    private var dlg: AlertDialog? = null

    @SuppressLint("ServiceCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
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

      binding.btnClient.setOnClickListener {
          val edtBox = EditText(this).apply {
              maxLines = 1
              filters = arrayOf(object : InputFilter {
                  override fun filter(
                      source: CharSequence?,
                      start: Int,
                      end: Int,
                      dest: Spanned?,
                      dstart: Int,
                      dend: Int
                  ): CharSequence? {
                      source?.run {
                          var ret = ""
                          forEach {
                              if (it.isDigit() || it == '.')
                                  ret += it
                          }
                          return ret
                      }
                      return null
                  }

              })
          }
          val dlg = AlertDialog.Builder(this)
              .setTitle(R.string.client_mode)
              .setMessage(R.string.ask_ip)
              .setPositiveButton(R.string.button_connect) { _: DialogInterface, _: Int ->
                  val strIP = edtBox.text.toString()
                  if (strIP.isEmpty() || !Patterns.IP_ADDRESS.matcher(strIP).matches()) {
                      Toast.makeText(this@MultiplayerLobby, R.string.error_address, Toast.LENGTH_LONG).show()
                      finish()
                  } else {
                      model.startClient(strIP)
                  }
              }

              .setNegativeButton(R.string.button_cancel) { _: DialogInterface, _: Int ->
                  finish()
              }
              .setCancelable(false)
              .setView(edtBox)
              .create()

          dlg.show()
           // startActivity(getClientModeIntent(this))

        }

        model.state.observe(this) {
            updateUI()
        }

        model.connectionState.observe(this) { state ->
            updateUI()
            if (state != MultiplayerLobbyViewModel.ConnectionState.SETTING_PARAMETERS &&
                state != MultiplayerLobbyViewModel.ConnectionState.SERVER_CONNECTING &&
                dlg?.isShowing == true) {
                dlg?.dismiss()
                dlg = null
            }

            if (state == MultiplayerLobbyViewModel.ConnectionState.CONNECTION_ERROR ||
                state == MultiplayerLobbyViewModel.ConnectionState.CONNECTION_ENDED ) {
                finish()
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                //to do: should ask if the user wants to finish
                model.stopGame()
            }
        })


    }

    override fun onPause() {
        super.onPause()
        dlg?.run {
            if (isShowing)
                dismiss()
        }
    }
    private fun updateUI() {
        if (model.connectionState.value != MultiplayerLobbyViewModel.ConnectionState.CONNECTION_ESTABLISHED) {
            return
        }
        model.startGame()

    }

}