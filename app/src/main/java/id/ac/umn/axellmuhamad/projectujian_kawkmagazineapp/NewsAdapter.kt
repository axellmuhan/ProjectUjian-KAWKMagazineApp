package id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class NewsAdapter(private var articleList: List<Article>) : RecyclerView.Adapter<NewsViewHolder>() {

    // Fungsi ini dipanggil untuk membuat ViewHolder baru
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return NewsViewHolder(view)
    }

    // Fungsi ini menghubungkan data pada posisi tertentu dengan ViewHolder
    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = articleList[position]
        holder.bind(article)
    }

    // Fungsi ini mengembalikan jumlah total item
    override fun getItemCount(): Int {
        return articleList.size
    }

    // Fungsi untuk memperbarui data di adapter
    fun setData(newArticleList: List<Article>) {
        this.articleList = newArticleList
        notifyDataSetChanged() // Memberi tahu RecyclerView untuk refresh
    }
}