package id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp // Sesuaikan

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View // Import View
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
import com.google.firebase.auth.FirebaseAuth // 1. Import Auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Locale

class ArticleDetailActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var commentAdapter: CommentAdapter
    private lateinit var auth: FirebaseAuth // 2. Variabel Auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_detail)

        // 3. Inisialisasi Auth
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        val imageView: ImageView = findViewById(R.id.article_image_detail)
        val titleView: TextView = findViewById(R.id.article_title_detail)
        val timestampView: TextView = findViewById(R.id.article_timestamp_detail)
        val contentView: TextView = findViewById(R.id.article_content_detail)
        val authorView: TextView = findViewById(R.id.article_author_detail)
        val likeButton: Button = findViewById(R.id.like_article_button)
        val commentsRecyclerView: RecyclerView = findViewById(R.id.detail_comments_recyclerview)
        val replyEditText: EditText = findViewById(R.id.detail_reply_edittext)
        val postButton: Button = findViewById(R.id.detail_post_button)

        // Ambil Data Intent
        val articleId = intent.getStringExtra("ARTICLE_ID")
        val title = intent.getStringExtra("ARTICLE_TITLE")
        val contentHtml = intent.getStringExtra("ARTICLE_CONTENT")
        val imageUrl = intent.getStringExtra("ARTICLE_IMAGE_URL")
        val timestamp = intent.getParcelableExtra<Timestamp>("ARTICLE_TIMESTAMP")
        val authorName = intent.getStringExtra("ARTICLE_AUTHOR")

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

        commentsRecyclerView.layoutManager = LinearLayoutManager(this)
        commentAdapter = CommentAdapter(mutableListOf(), articleId ?: "", this)
        commentsRecyclerView.adapter = commentAdapter

        // ===============================================
        // 🔒 LOGIKA GUEST MODE (PROTEKSI TOTAL) 🔒
        // ===============================================

        if (!articleId.isNullOrEmpty()) {
            fetchComments(articleId)

            // --- 1. PROTEKSI LIKE ---
            likeButton.setOnClickListener {
                if (currentUser == null) {
                    // Jika Tamu -> Tolak
                    Toast.makeText(this, "Login dulu untuk menyukai artikel!", Toast.LENGTH_SHORT).show()
                } else {
                    // Jika User -> Lanjut
                    val docRef = db.collection("articles").document(articleId)
                    docRef.update("likeCount", FieldValue.increment(1))
                }
            }

            // --- 2. PROTEKSI KOMENTAR (UI & KLIK) ---
            if (currentUser == null) {
                // JIKA TAMU:
                // Sembunyikan kotak ketik
                replyEditText.visibility = View.GONE

                // Ubah tombol Post jadi "LOGIN"
                postButton.text = "LOGIN UNTUK KOMEN"

                // Arahkan ke Login saat diklik
                postButton.setOnClickListener {
                    val intent = Intent(this, MainActivity::class.java) // Kembali ke Main (LoginFragment)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            } else {
                // JIKA USER:
                // Tampilan Normal
                replyEditText.visibility = View.VISIBLE
                postButton.text = "POST"

                postButton.setOnClickListener {
                    val commentText = replyEditText.text.toString()
                    if (commentText.isNotBlank()) {
                        postNewComment(articleId, commentText, replyEditText)
                    }
                }
            }

        } else {
            Toast.makeText(this, "Error: Data Artikel Tidak Ditemukan", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

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
                if (error != null) return@addSnapshotListener
                if (snapshots != null) {
                    val fetchedComments = mutableListOf<Comment>()
                    for (doc in snapshots.documents) {
                        val c = doc.toObject(Comment::class.java)
                        if (c != null) {
                            c.id = doc.id
                            fetchedComments.add(c)
                        }
                    }
                    commentAdapter.setData(fetchedComments)
                }
            }
    }

    private fun postNewComment(articleId: String, commentText: String, replyEditText: EditText) {
        // [PERBAIKAN] Ambil nama asli user
        val user = auth.currentUser
        val realName = user?.displayName ?: user?.email ?: "Anonymous"

        val articleDocRef = db.collection("articles").document(articleId)
        val newComment = Comment(
            authorName = realName,
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