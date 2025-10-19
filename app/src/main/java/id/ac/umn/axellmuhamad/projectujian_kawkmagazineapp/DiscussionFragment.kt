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

class DiscussionFragment : Fragment() {

    private lateinit var newsAdapter: NewsAdapter
    private lateinit var firestore: FirebaseFirestore
    private lateinit var newsRecyclerView: RecyclerView
    private val articleList = mutableListOf<Article>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_discussion, container, false)
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
            // Urutkan berdasarkan jumlah komentar, dari yang paling banyak ke paling sedikit
            .orderBy("commentCount", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, error ->

                if (error != null) {
                    Log.e("DiscussionFragment", "Error fetching articles", error) // Ganti tag log
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    val articleList = snapshots.toObjects(Article::class.java)
                    newsAdapter.setData(articleList)
                }
            }
    }
}