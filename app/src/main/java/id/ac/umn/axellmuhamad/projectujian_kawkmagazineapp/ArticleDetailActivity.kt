package id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp // Sesuaikan

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Locale

class ArticleDetailActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var commentAdapter: CommentAdapter
    // Kita tidak butuh variabel lokal 'commentList' lagi karena Adapter sudah pegang datanya
    // private val commentList = mutableListOf<Comment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_detail)

        // Cari semua komponen view
        val imageView: ImageView = findViewById(R.id.article_image_detail)
        val titleView: TextView = findViewById(R.id.article_title_detail)
        val timestampView: TextView = findViewById(R.id.article_timestamp_detail)
        val contentView: TextView = findViewById(R.id.article_content_detail)
        val authorView: TextView = findViewById(R.id.article_author_detail)
        val likeButton: Button = findViewById(R.id.like_article_button)
        val commentsRecyclerView: RecyclerView = findViewById(R.id.detail_comments_recyclerview)
        val replyEditText: EditText = findViewById(R.id.detail_reply_edittext)
        val postButton: Button = findViewById(R.id.detail_post_button)

        // Ambil data yang dikirim dari Intent
        val articleId = intent.getStringExtra("ARTICLE_ID")
        val title = intent.getStringExtra("ARTICLE_TITLE")
        val contentHtml = intent.getStringExtra("ARTICLE_CONTENT")
        val imageUrl = intent.getStringExtra("ARTICLE_IMAGE_URL")
        val timestamp = intent.getParcelableExtra<Timestamp>("ARTICLE_TIMESTAMP")
        val authorName = intent.getStringExtra("ARTICLE_AUTHOR")

        // Tampilkan data artikel
        titleView.text = title
        authorView.text = authorName

        if (timestamp != null) {
            val sdf = SimpleDateFormat("• dd MMMM yyyy, HH:mm", Locale.getDefault())
            timestampView.text = sdf.format(timestamp.toDate())
        }

        if (contentHtml != null) {
            contentView.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(contentHtml, Html.FROM_HTML_MODE_COMPACT)
            } else {
                @Suppress("DEPRECATION")
                Html.fromHtml(contentHtml)
            }
        }

        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.news_placeholder_1)
            .into(imageView)

        // --- SETUP RECYCLER VIEW ---
        commentsRecyclerView.layoutManager = LinearLayoutManager(this)

        // [PERBAIKAN UTAMA] Tambahkan 'this' sebagai parameter ke-3 (Context)
        commentAdapter = CommentAdapter(mutableListOf(), articleId ?: "", this)
        commentsRecyclerView.adapter = commentAdapter

        if (!articleId.isNullOrEmpty()) {
            // Hanya fetch jika ID valid
            fetchComments(articleId)

            likeButton.setOnClickListener {
                val docRef = db.collection("articles").document(articleId)
                docRef.update("likeCount", FieldValue.increment(1))
            }

            postButton.setOnClickListener {
                val commentText = replyEditText.text.toString()
                if(commentText.isNotBlank()){
                    postNewComment(articleId, commentText, replyEditText)
                }
            }
        } else {
            // Jika ID kosong, beritahu user dan tutup halaman biar gak crash
            Toast.makeText(this, "Error: Data Artikel Tidak Ditemukan", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    // [PENTING] Matikan ML saat Activity ditutup agar tidak bikin HP panas
    override fun onDestroy() {
        super.onDestroy()
        if (::commentAdapter.isInitialized) {
            commentAdapter.releaseResources()
        }
    }

    private fun fetchComments(articleId: String) {
        db.collection("articles").document(articleId)
            .collection("comments")
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.e("DetailComments", "Error fetching comments", error)
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    // Ambil data langsung jadi objek Comment
                    val fetchedComments = mutableListOf<Comment>()
                    for (doc in snapshots.documents) {
                        val c = doc.toObject(Comment::class.java)
                        if (c != null) {
                            c.id = doc.id
                            fetchedComments.add(c)
                        }
                    }

                    // Gunakan setData agar Adapter yang mengurus update UI
                    commentAdapter.setData(fetchedComments)
                }
            }
    }

    private fun postNewComment(articleId: String, commentText: String, replyEditText: EditText) {
        val articleDocRef = db.collection("articles").document(articleId)

        // Buat objek comment (pastikan Model Comment kamu punya field ini)
        val newComment = Comment(
            authorName = "You", // Nanti ganti dengan current user name
            commentText = commentText
        )

        db.runBatch { batch ->
            val newCommentRef = articleDocRef.collection("comments").document()
            batch.set(newCommentRef, newComment)
            batch.update(articleDocRef, "commentCount", FieldValue.increment(1))
        }
            .addOnSuccessListener {
                Toast.makeText(this, "Comment posted!", Toast.LENGTH_SHORT).show()
                replyEditText.text.clear()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to post: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}