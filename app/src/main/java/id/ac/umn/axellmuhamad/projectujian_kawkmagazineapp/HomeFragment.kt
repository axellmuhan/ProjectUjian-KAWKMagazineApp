package id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp // Sesuaikan

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HomeFragment : Fragment() {

    private lateinit var newsAdapter: NewsAdapter
    private lateinit var firestore: FirebaseFirestore
    private lateinit var newsRecyclerView: RecyclerView
    private val articleList = mutableListOf<Article>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()

        newsRecyclerView = view.findViewById(R.id.news_recyclerview)
        newsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        newsAdapter = NewsAdapter(articleList)
        newsRecyclerView.adapter = newsAdapter

        fetchArticlesFromFirestore()

        val fabNewPost: FloatingActionButton = view.findViewById(R.id.fab_new_post)
        fabNewPost.setOnClickListener {
            val intent = Intent(requireActivity(), NewPostActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchArticlesFromFirestore() {
        firestore.collection("articles")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.e("HomeFragment", "Error listening for article changes", error)
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    // ## BAGIAN YANG DIPERBARUI: Mengambil ID Dokumen ##
                    // Kita perlu iterasi manual untuk mendapatkan ID setiap dokumen.
                    val fetchedArticles = mutableListOf<Article>()
                    for (document in snapshots.documents) {
                        // Ubah dokumen menjadi objek Article
                        val article = document.toObject(Article::class.java)
                        if (article != null) {
                            // Simpan ID dokumen ke dalam objek Article
                            article.id = document.id
                            fetchedArticles.add(article)
                        }
                    }

                    // Kosongkan list lama dan isi dengan data baru
                    articleList.clear()
                    articleList.addAll(fetchedArticles)

                    // Beri tahu adapter bahwa data telah berubah
                    newsAdapter.notifyDataSetChanged()
                }
            }
    }
}