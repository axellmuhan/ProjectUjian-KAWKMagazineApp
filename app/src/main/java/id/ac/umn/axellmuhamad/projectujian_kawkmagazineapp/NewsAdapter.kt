package id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp // Sesuaikan

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

// [UBAH 1] Tambahkan 'private val context: Context' di constructor
class NewsAdapter(
    private var articleList: List<Article>,
    private val context: Context
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    // [UBAH 2] Inisialisasi SentimentHelper
    private val sentimentHelper = SentimentHelper(context)

    class NewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val authorNameTextView: TextView = view.findViewById(R.id.author_name)
        val postTextView: TextView = view.findViewById(R.id.post_text)
        val postImageView: ImageView = view.findViewById(R.id.post_image)
        val commentActionView: TextView = view.findViewById(R.id.comment_action)
        val likeActionView: TextView = view.findViewById(R.id.like_action)
        val postTimeTextView: TextView = view.findViewById(R.id.post_time)

        // [UBAH 3] Daftarkan ID TextView badge sentimen yang baru ditambahkan di XML
        val sentimentBadge: TextView = view.findViewById(R.id.tv_sentiment_badge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post_dark, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val currentArticle = articleList[position]

        holder.authorNameTextView.text = currentArticle.authorName
        holder.postTextView.text = currentArticle.title
        holder.likeActionView.text = currentArticle.likeCount.toString()
        holder.postTimeTextView.text = formatTimeAgo(currentArticle.createdAt)
        holder.commentActionView.text = currentArticle.commentCount.toString()

        // --- [UBAH 4] LOGIKA MACHINE LEARNING DI SINI ---
        // Kita analisis judul artikel (currentArticle.title)
        val textToAnalyze = currentArticle.title ?: ""

        if (textToAnalyze.isNotEmpty()) {
            // 1. Lakukan Prediksi
            val result = sentimentHelper.predict(textToAnalyze)

            // 2. Tampilkan Hasil Teks
            holder.sentimentBadge.text = result
            holder.sentimentBadge.visibility = View.VISIBLE

            // 3. Ubah Warna Background Badge Sesuai Hasil
            // (Warna hardcoded ini sesuai dengan saran XML sebelumnya)
            when {
                result.contains("Positif") -> {
                    // Hijau
                    holder.sentimentBadge.setBackgroundColor(Color.parseColor("#2E7D32"))
                }
                result.contains("Negatif") -> {
                    // Merah
                    holder.sentimentBadge.setBackgroundColor(Color.parseColor("#C62828"))
                }
                else -> {
                    // Netral (Abu-abu Biru)
                    holder.sentimentBadge.setBackgroundColor(Color.parseColor("#455A64"))
                }
            }
        } else {
            // Sembunyikan jika tidak ada teks judul
            holder.sentimentBadge.visibility = View.GONE
        }
        // --------------------------------------------------

        Glide.with(holder.itemView.context)
            .load(currentArticle.imageUrl)
            .placeholder(R.drawable.news_placeholder_1)
            .into(holder.postImageView)

        holder.itemView.setOnClickListener {
            val ctx = holder.itemView.context
            val intent = Intent(ctx, ArticleDetailActivity::class.java).apply {
                putExtra("ARTICLE_TITLE", currentArticle.title)
                putExtra("ARTICLE_CONTENT", currentArticle.content)
                putExtra("ARTICLE_IMAGE_URL", currentArticle.imageUrl)
                putExtra("ARTICLE_TIMESTAMP", currentArticle.createdAt)
                putExtra("ARTICLE_AUTHOR", currentArticle.authorName)
                putExtra("ARTICLE_ID", currentArticle.id)
            }
            ctx.startActivity(intent)
        }

        holder.commentActionView.setOnClickListener {
            val ctx = holder.itemView.context
            val fragmentManager = (ctx as? AppCompatActivity)?.supportFragmentManager
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

    // [UBAH 5] Fungsi bersih-bersih memori (dipanggil dari Fragment/Activity)
    fun releaseResources() {
        sentimentHelper.close()
    }
}