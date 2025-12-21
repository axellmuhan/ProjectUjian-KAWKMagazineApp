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

    private lateinit var newsAdapter: NewsAdapter
    private lateinit var firestore: FirebaseFirestore
    private lateinit var newsRecyclerView: RecyclerView
    private val articleList = mutableListOf<Article>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_discussion, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firestore = FirebaseFirestore.getInstance()

        // Pastikan ID ini sesuai XML kamu (news_recyclerview)
        newsRecyclerView = view.findViewById(R.id.news_recyclerview)
        newsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Memasukkan list dan context agar NewsAdapter jalan
        newsAdapter = NewsAdapter(articleList, requireContext())
        newsRecyclerView.adapter = newsAdapter

        fetchArticlesFromFirestore()
    }

    private fun fetchArticlesFromFirestore() {
        firestore.collection("articles")
            .orderBy("commentCount", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.e("DiscussionFragment", "Error", error)
                    return@addSnapshotListener
                }
                if (snapshots != null) {
                    val list = snapshots.toObjects(Article::class.java)
                    newsAdapter.setData(list)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        newsAdapter.releaseResources()
    }
}