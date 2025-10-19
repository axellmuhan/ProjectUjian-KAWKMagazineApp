package id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp // Sesuaikan

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class SearchFragment : Fragment() {

    private lateinit var searchAdapter: SearchAdapter
    private val searchResults = mutableListOf<Article>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.search_news_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(context)
        searchAdapter = SearchAdapter(searchResults)
        recyclerView.adapter = searchAdapter

        val searchEditText: EditText = view.findViewById(R.id.search_edit_text)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                if (query.isNotEmpty()) {
                    performSearch(query)
                } else {
                    searchResults.clear()
                    searchAdapter.updateData(emptyList())
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        setupTagClickListeners(view, searchEditText)
    }

    private fun setupTagClickListeners(view: View, searchEditText: EditText) {
        val tags = listOf<TextView>(
            view.findViewById(R.id.tag_gaya_hidup), view.findViewById(R.id.tag_olahraga),
            view.findViewById(R.id.tag_lari), view.findViewById(R.id.tag_kesehatan),
            view.findViewById(R.id.tag_sains), view.findViewById(R.id.tag_teknologi),
            view.findViewById(R.id.tag_travel), view.findViewById(R.id.tag_kuliner)
        )
        tags.forEach { tagView ->
            tagView.setOnClickListener {
                searchEditText.setText(tagView.text)
            }
        }
    }

    // ## BAGIAN YANG DIPERBAIKI: Mengambil ID unik setiap artikel ##
    private fun performSearch(query: String) {
        val searchQueryLower = query.lowercase()

        db.collection("articles")
            .whereArrayContains("tags", searchQueryLower)
            .limit(15)
            .get()
            .addOnSuccessListener { documents ->
                val articles = mutableListOf<Article>()
                for (document in documents) {
                    val article = document.toObject(Article::class.java)
                    if (article != null) {
                        // Simpan ID dokumen ke dalam objek Article
                        article.id = document.id
                        articles.add(article)
                    }
                }
                searchAdapter.updateData(articles)
                Log.d("FirestoreSearch", "Found ${articles.size} articles for query: $searchQueryLower")
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreSearch", "Error getting documents: ", exception)
            }
    }
}