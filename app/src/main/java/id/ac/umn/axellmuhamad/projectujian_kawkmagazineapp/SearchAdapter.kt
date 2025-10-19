package id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp // Sesuaikan

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide // <-- Pastikan import ini ada

class SearchAdapter(private var results: List<Article>) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    class SearchViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.news_title_search)
        val imageView: ImageView = view.findViewById(R.id.news_image_search)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_news, parent, false)
        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val article = results[position]

        // Mengisi judul (sudah benar)
        holder.titleTextView.text = article.title

        // ## BAGIAN YANG DIPERBARUI: MEMUAT GAMBAR DARI URL DENGAN GLIDE ##
        Glide.with(holder.itemView.context)
            .load(article.imageUrl) // <-- Ambil URL dari field imageUrl di data Anda
            .placeholder(R.drawable.news_placeholder_1) // <-- Gunakan placeholder yang sudah ada
            .into(holder.imageView) // <-- Masukkan gambar ke ImageView

        // Membuat seluruh item bisa diklik (sudah benar)
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ArticleDetailActivity::class.java).apply {
                putExtra("ARTICLE_TITLE", article.title)
                putExtra("ARTICLE_CONTENT", article.content)
                putExtra("ARTICLE_IMAGE_URL", article.imageUrl)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = results.size

    fun updateData(newResults: List<Article>) {
        results = newResults
        notifyDataSetChanged()
    }
}