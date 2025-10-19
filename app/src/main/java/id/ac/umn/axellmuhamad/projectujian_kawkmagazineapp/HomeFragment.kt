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

class HomeFragment : Fragment() {

    private lateinit var newsAdapter: NewsAdapter
    private lateinit var firestore: FirebaseFirestore
    private lateinit var newsRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Menghubungkan file Kotlin ini dengan layout XML-nya
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Inisialisasi Firestore
        firestore = FirebaseFirestore.getInstance()

        // 2. Setup RecyclerView
        newsRecyclerView = view.findViewById(R.id.news_recyclerview)
        newsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Buat adapter dengan list kosong terlebih dahulu
        // Kita akan isi datanya nanti dari Firestore
        newsAdapter = NewsAdapter(emptyList())
        newsRecyclerView.adapter = newsAdapter

        // 3. Panggil fungsi untuk mulai mengambil data
        fetchArticlesFromFirestore()
    }

    private fun fetchArticlesFromFirestore() {
        // Mengambil data dari koleksi "articles"
        firestore.collection("articles")
            // Mengurutkan berdasarkan waktu dibuat, dari yang terbaru ke terlama
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, error ->

                // Jika ada error saat mengambil data, hentikan dan catat di Logcat
                if (error != null) {
                    Log.e("HomeFragment", "Error listening for article changes", error)
                    return@addSnapshotListener
                }

                // Jika data berhasil diambil (tidak null)
                if (snapshots != null) {
                    // Ubah semua dokumen dari Firestore menjadi List<Article>
                    val articleList = snapshots.toObjects(Article::class.java)

                    // Kirim data baru ke adapter agar ditampilkan di RecyclerView
                    newsAdapter.setData(articleList)
                }
            }
    }
}