package id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dummyPosts = listOf(
            NewsPost("KAWK News", "Apple Merilis Vision Pro Generasi Terbaru", R.drawable.news_placeholder_1),
            NewsPost("Tekno Mania", "Google I/O 2025: Apa Saja yang Baru dari Android?", R.drawable.news_placeholder_1),
            NewsPost("KAWK News", "Tips & Trik Fotografi dengan Smartphone", R.drawable.news_placeholder_1),
            NewsPost("Tekno Mania", "Review Laptop Gaming Paling Tipis di Dunia", R.drawable.news_placeholder_1),
            NewsPost("KAWK News", "Masa Depan Kecerdasan Buatan (AI)", R.drawable.news_placeholder_1)
        )

        val newsRecyclerView: RecyclerView = view.findViewById(R.id.news_recyclerview)
        newsRecyclerView.adapter = NewsAdapter(dummyPosts)
    }
}