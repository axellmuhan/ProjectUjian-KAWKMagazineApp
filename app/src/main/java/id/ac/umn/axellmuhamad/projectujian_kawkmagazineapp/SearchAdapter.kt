package id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp // Sesuaikan

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

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

        holder.titleTextView.text = article.title

        Glide.with(holder.itemView.context)
            .load(article.imageUrl)
            .placeholder(R.drawable.news_placeholder_1)
            .into(holder.imageView)

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ArticleDetailActivity::class.java).apply {
                putExtra("ARTICLE_TITLE", article.title)
                putExtra("ARTICLE_CONTENT", article.content)
                putExtra("ARTICLE_IMAGE_URL", article.imageUrl)
                putExtra("ARTICLE_TIMESTAMP", article.createdAt)
                putExtra("ARTICLE_AUTHOR", article.authorName) // <-- Tambahan baru
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