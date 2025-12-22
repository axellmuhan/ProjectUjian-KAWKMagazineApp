package id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class DiscussionFragment : Fragment() {

    // 👇 UBAH 1: Ganti tipe variabel dari NewsAdapter ke DiscussionAdapter
    private lateinit var discussionAdapter: DiscussionAdapter

    private lateinit var firestore: FirebaseFirestore
    private lateinit var newsRecyclerView: RecyclerView
    private val articleList = mutableListOf<Article>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_discussion, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firestore = FirebaseFirestore.getInstance()

        newsRecyclerView = view.findViewById(R.id.news_recyclerview)
        newsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 👇 UBAH 2: Inisialisasi DiscussionAdapter (bukan NewsAdapter)
        discussionAdapter = DiscussionAdapter(articleList, requireContext())
        newsRecyclerView.adapter = discussionAdapter

        fetchArticlesFromFirestore()
    }

    private fun fetchArticlesFromFirestore() {
        firestore.collection("articles")
            .orderBy("commentCount", Query.Direction.DESCENDING) // Tetap urutkan berdasarkan diskusi teramai
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.e("DiscussionFragment", "Error", error)
                    return@addSnapshotListener
                }
                if (snapshots != null) {
                    val list = mutableListOf<Article>()
                    for (document in snapshots.documents) {
                        val article = document.toObject(Article::class.java)
                        if (article != null) {
                            // ✅ PENTING: ID tetap diambil manual agar tidak crash saat diklik
                            article.id = document.id
                            list.add(article)
                        }
                    }

                    // 👇 UBAH 3: Panggil setData milik discussionAdapter
                    discussionAdapter.setData(list)
                }
            }
    }
}