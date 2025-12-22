package id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp

import android.content.Context
import android.content.Intent
import android.util.Log
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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

// Hapus parameter ML, biarkan Context karena Bookmark butuh Context buat Toast
class NewsAdapter(
    private var articleList: List<Article>,
    private val context: Context
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    class NewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val authorName: TextView = view.findViewById(R.id.author_name)
        val postText: TextView = view.findViewById(R.id.post_text)
        val postImage: ImageView = view.findViewById(R.id.post_image)
        val likeAction: TextView = view.findViewById(R.id.like_action)
        val commentAction: TextView = view.findViewById(R.id.comment_action)
        val saveAction: ImageView = view.findViewById(R.id.save_action)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post_dark, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = articleList[position]
        val user = auth.currentUser

        holder.authorName.text = article.authorName
        holder.postText.text = article.title
        holder.likeAction.text = article.likeCount.toString()
        holder.commentAction.text = article.commentCount.toString()

        // --- BAGIAN ML/SENTIMEN SUDAH DIHAPUS DI SINI ---

        // Bookmark Icon Default
        holder.saveAction.setImageResource(R.drawable.ic_bookmark_outline)

        // Klik Berita ke Detail
        holder.itemView.setOnClickListener {
            Log.d("NewsAdapter", "Mengirim ID Artikel: ${article.id}")
            val intent = Intent(context, ArticleDetailActivity::class.java).apply {
                putExtra("ARTICLE_ID", article.id)
                putExtra("ARTICLE_TITLE", article.title)
                putExtra("ARTICLE_CONTENT", article.content)
                putExtra("ARTICLE_IMAGE_URL", article.imageUrl)
                putExtra("ARTICLE_AUTHOR", article.authorName)
                putExtra("ARTICLE_TIMESTAMP", article.createdAt)
            }
            context.startActivity(intent)
        }

        // Like Logic
        holder.likeAction.setOnClickListener {
            if (article.id.isNotEmpty()) {
                db.collection("articles").document(article.id)
                    .update("likeCount", FieldValue.increment(1))
            }
        }

        // Comment Logic
        holder.commentAction.setOnClickListener {
            if (article.id.isNotEmpty()) {
                val commentSheet = CommentBottomSheetFragment.newInstance(article.id)
                val fragmentManager = (context as AppCompatActivity).supportFragmentManager
                commentSheet.show(fragmentManager, "CommentBottomSheet")
            }
        }

        // --- BOOKMARK LOGIC (DIJAGA TETAP SAMA SEPERTI TEMANMU) ---
        holder.saveAction.setOnClickListener {
            if (user != null && article.id.isNotEmpty()) {
                val docRef = db.collection("users").document(user.uid)
                    .collection("saved_articles").document(article.id)
                docRef.get().addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        docRef.delete().addOnSuccessListener {
                            Toast.makeText(context, "Removed from Bookmarks!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        docRef.set(article).addOnSuccessListener {
                            Toast.makeText(context, "Added to Bookmarks!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
        // -----------------------------------------------------------

        Glide.with(context)
            .load(article.imageUrl)
            .into(holder.postImage)
    }

    override fun getItemCount() = articleList.size

    fun setData(newList: List<Article>) {
        articleList = newList
        notifyDataSetChanged()
    }
}