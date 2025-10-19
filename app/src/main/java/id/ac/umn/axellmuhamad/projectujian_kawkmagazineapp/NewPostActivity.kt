package id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp // Sesuaikan

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
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.util.UUID

class NewPostActivity : AppCompatActivity() {

    private lateinit var imagePreview: ImageView
    private var latestTmpUri: Uri? = null

    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    // ## TAMBAHAN BARU: Deklarasi Launcher yang Hilang ##
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
    // ---------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_post)

        imagePreview = findViewById(R.id.image_preview)
        val libraryButton: TextView = findViewById(R.id.library_button)
        val cameraButton: TextView = findViewById(R.id.camera_button)
        val cancelButton: TextView = findViewById(R.id.cancel_post_button)
        val postButton: TextView = findViewById(R.id.post_button)
        val postEditText: EditText = findViewById(R.id.post_edit_text)

        // --- Logika Tombol ---
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
            if (postText.isBlank()) {
                Toast.makeText(this, "Tulisan tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (latestTmpUri != null) {
                uploadImageAndSaveArticle(postText, latestTmpUri!!)
            } else {
                saveArticle(postText, "")
            }
        }
    }

    private fun uploadImageAndSaveArticle(postText: String, imageUri: Uri) {
        val fileName = "images/${UUID.randomUUID()}.jpg"
        val storageRef = storage.reference.child(fileName)

        Toast.makeText(this, "Mengunggah postingan...", Toast.LENGTH_SHORT).show()

        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    saveArticle(postText, downloadUrl.toString())
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Gagal mengunggah gambar: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun saveArticle(postText: String, imageUrl: String) {
        val newArticle = Article(
            title = postText.substring(0, minOf(postText.length, 30)),
            content = "<p>$postText</p>",
            authorId = "user_001",
            authorName = "Karrell Wilson",
            imageUrl = imageUrl,
            categoryName = "General",
            createdAt = Timestamp.now(),
            updatedAt = Timestamp.now(),
            isPublished = true,
            isFeatured = false,
            tags = listOf(postText.substring(0, minOf(postText.length, 5)).lowercase())
        )

        firestore.collection("articles")
            .add(newArticle)
            .addOnSuccessListener {
                Toast.makeText(this, "Berhasil diposting!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Gagal memposting: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun takeImage() {
        val photoFile = File.createTempFile("IMG_", ".jpg", cacheDir)
        val fileUri = FileProvider.getUriForFile(this, "${applicationContext.packageName}.provider", photoFile)
        latestTmpUri = fileUri
        takeImageLauncher.launch(fileUri)
    }
}