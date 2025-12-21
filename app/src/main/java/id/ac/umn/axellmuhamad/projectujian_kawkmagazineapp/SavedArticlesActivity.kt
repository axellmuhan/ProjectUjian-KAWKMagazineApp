package id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SavedArticlesActivity : AppCompatActivity() {

    private lateinit var adapter: NewsAdapter
    private lateinit var tvEmpty: TextView
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_articles)

        // Inisialisasi UI dengan ID yang sudah disinkronkan
        tvEmpty = findViewById(R.id.tv_empty_saved)
        val btnBack = findViewById<ImageView>(R.id.btn_back_saved)
        val recyclerView = findViewById<RecyclerView>(R.id.rv_saved_articles)

        btnBack.setOnClickListener { finish() }

        // Setup RecyclerView dan Adapter
        adapter = NewsAdapter(mutableListOf(), this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        loadSavedArticles()
    }

    private fun loadSavedArticles() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            // SnapshotListener memantau perubahan data secara real-time
            db.collection("users").document(userId)
                .collection("saved_articles")
                .addSnapshotListener { snapshots, e ->
                    if (e != null) {
                        return@addSnapshotListener
                    }

                    val savedList = mutableListOf<Article>()
                    snapshots?.forEach { doc ->
                        val article = doc.toObject(Article::class.java)
                        article.id = doc.id
                        savedList.add(article)
                    }

                    // Tampilkan pesan kosong jika tidak ada data
                    if (savedList.isEmpty()) {
                        tvEmpty.visibility = View.VISIBLE
                    } else {
                        tvEmpty.visibility = View.GONE
                    }

                    // Update data ke adapter
                    adapter.setData(savedList)
                }
        }
    }
}