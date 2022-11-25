package pt.isec.amov.tp1

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.snackbar.Snackbar
import pt.isec.amov.tp1.databinding.ActivityEditProfileBinding
import java.io.File
import java.io.FileOutputStream

class EditProfileActivity : AppCompatActivity() {

    companion object {
        private const val PERMISSIONS_REQUEST_CODE = 10
    }

    private lateinit var binding : ActivityEditProfileBinding
    private var permissionsGranted = false
        set(value){
            field = value
            binding.changePicBtn.isEnabled = value
        }
    private var imagePath : String? = null
    private var imageBitmap : Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(loadImage(this, "avatar.jpg") != null) {
            binding.playerAvatar2.setImageBitmap(loadImage(this, "avatar.jpg"))
        }else{
            binding.playerAvatar2.setImageDrawable(getDrawable(R.drawable.untitled_1))
        }
        if(getUsername(this) != null){
            binding.usernameTextField.setText(getUsername(this))
        } else {
            binding.usernameTextField.setText(R.string.jogador.toString())
        }

        binding.changePicBtn.setOnClickListener{ chooseImage() }
        binding.saveBtn.isEnabled = false
        binding.saveBtn.setOnClickListener{
            if(imagePath != null){
                imageBitmap?.let { it1 -> saveImage(this, it1, "avatar.jpg") }
            }
            if(binding.usernameTextField.text.toString() != ""){
                saveUsername(this, binding.usernameTextField.text.toString())
            }
            binding.saveBtn.isEnabled = false
            Snackbar.make(binding.root, "Profile updated successfully!", Snackbar.LENGTH_SHORT).show()
            Intent(this, MainActivity::class.java).also {
                startActivity(it)
            }
        }
        binding.usernameTextField.addTextChangedListener { binding.saveBtn.isEnabled = true }

        verifyPermissions()
        loadImage(this, "avatar.jpg")?.let{ binding.playerAvatar2.setImageBitmap(it) }
    }

    private val requestPermissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
        grantedResults -> permissionsGranted = grantedResults.values.all{ it }
    }

    private fun verifyPermissions(){
        requestPermissionsLauncher.launch(arrayOf(
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == PERMISSIONS_REQUEST_CODE){
            permissionsGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private val startActivityForResult = registerForActivityResult(
        ActivityResultContracts.GetContent())
    { uri ->
        imagePath = uri?.let {
            createFileFromUri(this, it)
        }
        updateView()
    }

    private fun updateView(){
        if(imagePath != null){
            val file = File(imagePath ?: "")
            imageBitmap = BitmapFactory.decodeFile(file.absolutePath)
            val drawable = BitmapDrawable(resources, imageBitmap)
            binding.playerAvatar2.setImageDrawable(drawable)
            binding.playerAvatar2.clipToOutline = true
            binding.saveBtn.isEnabled = true
        }
    }

    private fun chooseImage(){
        startActivityForResult.launch("image/*")
    }

}