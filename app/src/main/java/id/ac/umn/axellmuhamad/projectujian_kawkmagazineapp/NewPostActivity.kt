package id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File

class NewPostActivity : AppCompatActivity() {

    private lateinit var imagePreview: ImageView
    private var latestTmpUri: Uri? = null

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imagePreview.setImageURI(it)
            imagePreview.visibility = View.VISIBLE
            latestTmpUri = it
        }
    }

    private val takeImageLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess: Boolean ->
        if (isSuccess) {
            latestTmpUri?.let {
                imagePreview.setImageURI(it)
                imagePreview.visibility = View.VISIBLE
            }
        }
    }

    private val cameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            takeImage()
        } else {
            Toast.makeText(this, "Izin kamera diperlukan", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_post)

        imagePreview = findViewById(R.id.image_preview)
        val libraryButton: TextView = findViewById(R.id.library_button)
        val cameraButton: TextView = findViewById(R.id.camera_button)
        val cancelButton: TextView = findViewById(R.id.cancel_post_button)
        val postButton: TextView = findViewById(R.id.post_button)
        val postEditText: EditText = findViewById(R.id.post_edit_text)

        cancelButton.setOnClickListener {
            finish()
        }

        libraryButton.setOnClickListener {
            selectImageLauncher.launch("image/*")
        }

        cameraButton.setOnClickListener {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        postButton.setOnClickListener {
            val postText = postEditText.text.toString()
            if (postText.isNotBlank()) {
                val resultIntent = Intent()
                resultIntent.putExtra("NEW_POST_TEXT", postText)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }
    }

    private fun takeImage() {
        val photoFile = File.createTempFile("IMG_", ".jpg", cacheDir)
        val fileUri = FileProvider.getUriForFile(this, "${applicationContext.packageName}.provider", photoFile)
        latestTmpUri = fileUri
        takeImageLauncher.launch(fileUri)
    }
}