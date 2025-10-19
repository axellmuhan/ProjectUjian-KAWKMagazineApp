package id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp // Sesuaikan

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide // <-- Pastikan import ini ada
import com.google.firebase.firestore.FirebaseFirestore

class NewsAdapter(private var articleList: List<Article>) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    class NewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val authorNameTextView: TextView = view.findViewById(R.id.author_name)
        val postTextView: TextView = view.findViewById(R.id.post_text)
        val postImageView: ImageView = view.findViewById(R.id.post_image)
        val commentActionView: TextView = view.findViewById(R.id.comment_action)
        val likeActionView: TextView = view.findViewById(R.id.like_action)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post_dark, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val currentArticle = articleList[position]

        // Mengisi data teks
        holder.authorNameTextView.text = currentArticle.authorName
        holder.postTextView.text = currentArticle.title
        holder.likeActionView.text = currentArticle.likeCount.toString()

        // ## BAGIAN YANG DIPERBARUI: MEMUAT GAMBAR DARI URL ##
        Glide.with(holder.itemView.context)
            .load(currentArticle.imageUrl) // <-- Ambil URL dari objek Article
            .placeholder(R.drawable.news_placeholder_1) // Gambar sementara saat loading
            .into(holder.postImageView) // Masukkan gambar ke ImageView

        // --- Logika Klik ---
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ArticleDetailActivity::class.java).apply {
                putExtra("ARTICLE_TITLE", currentArticle.title)
                putExtra("ARTICLE_CONTENT", currentArticle.content)
                putExtra("ARTICLE_IMAGE_URL", currentArticle.imageUrl)
            }
            context.startActivity(intent)
        }

        holder.commentActionView.setOnClickListener {
            val context = holder.itemView.context
            val fragmentManager = (context as? AppCompatActivity)?.supportFragmentManager
            if (fragmentManager != null) {
                val commentSheet = CommentBottomSheetFragment.newInstance(currentArticle.id)
                commentSheet.show(fragmentManager, "CommentBottomSheetFragment")
            }
        }

        holder.likeActionView.setOnClickListener {
            val articleId = currentArticle.id
            if (articleId.isNotBlank()) {
                val docRef = FirebaseFirestore.getInstance().collection("articles").document(articleId)
                val newLikes = currentArticle.likeCount + 1
                docRef.update("likeCount", newLikes)
            }
        }
    }

    override fun getItemCount(): Int {
        return articleList.size
    }

    fun setData(newArticleList: List<Article>) {
        this.articleList = newArticleList
        notifyDataSetChanged()
    }
}