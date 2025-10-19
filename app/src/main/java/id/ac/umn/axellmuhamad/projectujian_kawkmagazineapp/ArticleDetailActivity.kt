package id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp // Sesuaikan

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

class ArticleDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_detail)

        // Cari semua komponen view dari layout
        val imageView: ImageView = findViewById(R.id.article_image_detail)
        val titleView: TextView = findViewById(R.id.article_title_detail)
        val timestampView: TextView = findViewById(R.id.article_timestamp_detail)
        val contentView: TextView = findViewById(R.id.article_content_detail)
        val authorView: TextView = findViewById(R.id.article_author_detail) // <-- Tambahan baru

        // Ambil data yang dikirim dari adapter
        val title = intent.getStringExtra("ARTICLE_TITLE")
        val contentHtml = intent.getStringExtra("ARTICLE_CONTENT")
        val imageUrl = intent.getStringExtra("ARTICLE_IMAGE_URL")
        val timestamp = intent.getParcelableExtra<Timestamp>("ARTICLE_TIMESTAMP")
        val authorName = intent.getStringExtra("ARTICLE_AUTHOR") // <-- Tambahan baru

        // Tampilkan data ke komponen view
        titleView.text = title
        authorView.text = authorName // <-- Tambahan baru

        // Format dan tampilkan timestamp dengan jam
        if (timestamp != null) {
            val sdf = SimpleDateFormat("• dd MMMM yyyy, HH:mm", Locale.getDefault())
            timestampView.text = sdf.format(timestamp.toDate())
        }

        // Menampilkan konten yang formatnya HTML
        if (contentHtml != null) {
            contentView.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(contentHtml, Html.FROM_HTML_MODE_COMPACT)
            } else {
                @Suppress("DEPRECATION")
                Html.fromHtml(contentHtml)
            }
        }

        // Memuat gambar dari URL menggunakan Glide
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.news_placeholder_1)
            .into(imageView)
    }
}