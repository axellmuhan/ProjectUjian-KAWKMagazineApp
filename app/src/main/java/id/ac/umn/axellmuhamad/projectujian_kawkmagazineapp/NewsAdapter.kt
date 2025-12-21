package id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NewsAdapter(
    private var articleList: List<Article>,
    private val context: Context
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    private val sentimentHelper = SentimentHelper(context)

    class NewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val authorName: TextView = view.findViewById(R.id.author_name)
        val postText: TextView = view.findViewById(R.id.post_text)
        val postImage: ImageView = view.findViewById(R.id.post_image)
        val postTime: TextView = view.findViewById(R.id.post_time)
        val sentimentBadge: TextView = view.findViewById(R.id.tv_sentiment_badge)
        val likeAction: TextView = view.findViewById(R.id.like_action)
        val commentAction: TextView = view.findViewById(R.id.comment_action)
        val saveAction: ImageView = view.findViewById(R.id.save_action)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post_dark, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val currentArticle = articleList[position]

        holder.authorName.text = currentArticle.authorName
        holder.postText.text = currentArticle.title
        holder.likeAction.text = currentArticle.likeCount.toString()
        holder.commentAction.text = currentArticle.commentCount.toString()

        // --- Logika Sentiment Analysis ---
        val textToAnalyze = currentArticle.title ?: ""
        if (textToAnalyze.isNotEmpty()) {
            val result = sentimentHelper.predict(textToAnalyze)
            holder.sentimentBadge.text = result
            holder.sentimentBadge.visibility = View.VISIBLE
            when {
                result.contains("Positif") -> holder.sentimentBadge.setBackgroundColor(Color.parseColor("#2E7D32"))
                result.contains("Negatif") -> holder.sentimentBadge.setBackgroundColor(Color.parseColor("#C62828"))
                else -> holder.sentimentBadge.setBackgroundColor(Color.parseColor("#455A64"))
            }
        }

        // --- [PENTING] Logika Toggle Save / Unsave ---
        holder.saveAction.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                val db = FirebaseFirestore.getInstance()
                val docRef = db.collection("users").document(user.uid)
                    .collection("saved_articles").document(currentArticle.id)

                docRef.get().addOnSuccessListener { document ->
                    if (document.exists()) {
                        // JIKA SUDAH ADA -> HAPUS (UNSAVE)
                        docRef.delete().addOnSuccessListener {
                            Toast.makeText(context, "Berita dihapus dari simpanan", Toast.LENGTH_SHORT).show()

                            // Jika sedang di halaman SavedArticlesActivity, langsung hapus dari list UI
                            if (context is SavedArticlesActivity) {
                                val currentList = articleList.toMutableList()
                                currentList.removeAt(position)
                                setData(currentList)
                            }
                        }
                    } else {
                        // JIKA BELUM ADA -> SIMPAN (SAVE)
                        docRef.set(currentArticle).addOnSuccessListener {
                            Toast.makeText(context, "Berita berhasil disimpan!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(context, "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show()
            }
        }

        Glide.with(context).load(currentArticle.imageUrl).placeholder(R.drawable.news_placeholder_1).into(holder.postImage)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ArticleDetailActivity::class.java).apply {
                putExtra("ARTICLE_TITLE", currentArticle.title)
                putExtra("ARTICLE_CONTENT", currentArticle.content)
                putExtra("ARTICLE_IMAGE_URL", currentArticle.imageUrl)
                putExtra("ARTICLE_AUTHOR", currentArticle.authorName)
                putExtra("ARTICLE_ID", currentArticle.id)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = articleList.size

    fun setData(newArticleList: List<Article>) {
        this.articleList = newArticleList
        notifyDataSetChanged()
    }

    fun releaseResources() {
        sentimentHelper.close()
    }
}