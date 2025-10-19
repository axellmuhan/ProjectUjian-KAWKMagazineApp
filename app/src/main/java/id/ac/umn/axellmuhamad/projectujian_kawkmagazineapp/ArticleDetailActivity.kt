package id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp // Sesuaikan

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class ArticleDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_detail)

        // Cari semua komponen view dari layout
        val imageView: ImageView = findViewById(R.id.article_image_detail)
        val titleView: TextView = findViewById(R.id.article_title_detail)
        val contentView: TextView = findViewById(R.id.article_content_detail)

        // Ambil data yang dikirim dari adapter
        val title = intent.getStringExtra("ARTICLE_TITLE")
        val contentHtml = intent.getStringExtra("ARTICLE_CONTENT")
        val imageUrl = intent.getStringExtra("ARTICLE_IMAGE_URL")

        // Tampilkan data ke komponen view
        titleView.text = title

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
            .placeholder(R.drawable.news_placeholder_1) // Gambar sementara saat loading
            .into(imageView)
    }
}