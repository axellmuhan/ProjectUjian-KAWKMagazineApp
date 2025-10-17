package id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class ImageViewerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_viewer)

        val fullScreenImageView: ImageView = findViewById(R.id.fullScreenImageView)

        if (intent.hasExtra("IMAGE_RES_ID")) {
            val imageResId = intent.getIntExtra("IMAGE_RES_ID", 0)
            if (imageResId != 0) {
                fullScreenImageView.setImageResource(imageResId)
            }
        }
    }
}