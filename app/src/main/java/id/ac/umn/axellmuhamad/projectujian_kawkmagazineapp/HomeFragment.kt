package id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeFragment : Fragment() {

    private lateinit var newsAdapter: NewsAdapter
    private val postList = mutableListOf<NewsPost>()

    private val postLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val newPostText = result.data?.getStringExtra("NEW_POST_TEXT")

            if (!newPostText.isNullOrBlank()) {
                val newPost = NewsPost("You", newPostText, R.drawable.news_placeholder_1)

                postList.add(0, newPost)

                newsAdapter.notifyItemInserted(0)

                view?.findViewById<RecyclerView>(R.id.news_recyclerview)?.scrollToPosition(0)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupInitialPosts()

        val newsRecyclerView: RecyclerView = view.findViewById(R.id.news_recyclerview)
        newsAdapter = NewsAdapter(postList)
        newsRecyclerView.adapter = newsAdapter

        val fabNewPost: FloatingActionButton = view.findViewById(R.id.fab_new_post)
        fabNewPost.setOnClickListener {
            val intent = Intent(requireActivity(), NewPostActivity::class.java)
            postLauncher.launch(intent)
        }
    }

    private fun setupInitialPosts() {
        postList.clear()
        postList.addAll(listOf(
            NewsPost("KAWK News", "Apple Merilis Vision Pro Generasi Terbaru", R.drawable.news_placeholder_1),
            NewsPost("Tekno Mania", "Google I/O 2025: Apa Saja yang Baru dari Android?", R.drawable.news_placeholder_1)
        ))
    }
}