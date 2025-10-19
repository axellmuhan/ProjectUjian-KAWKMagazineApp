package id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val postImage: ImageView = itemView.findViewById(R.id.post_image)
    private val postTitle: TextView = itemView.findViewById(R.id.post_title)
    private val postAuthor: TextView = itemView.findViewById(R.id.post_author)

    fun bind(article: Article) {
        postTitle.text = article.title
        postAuthor.text = article.authorName

        Glide.with(itemView.context)
            .load(article.imageUrl) // Memuat URL dari data
            .placeholder(R.drawable.news_placeholder_1) // Gambar saat loading (opsional)
            .error(R.drawable.news_placeholder_1)       // Gambar jika gagal (opsional)
            .into(postImage)
    }
}