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
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class NewsAdapter(private var articleList: List<Article>) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    class NewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val authorNameTextView: TextView = view.findViewById(R.id.author_name)
        val postTextView: TextView = view.findViewById(R.id.post_text)
        val postImageView: ImageView = view.findViewById(R.id.post_image)
        val commentActionView: TextView = view.findViewById(R.id.comment_action)
        val likeActionView: TextView = view.findViewById(R.id.like_action)
        val postTimeTextView: TextView = view.findViewById(R.id.post_time)
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

        Glide.with(holder.itemView.context)
            .load(currentArticle.imageUrl)
            .placeholder(R.drawable.news_placeholder_1)
            .into(holder.postImageView)

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ArticleDetailActivity::class.java).apply {
                putExtra("ARTICLE_TITLE", currentArticle.title)
                putExtra("ARTICLE_CONTENT", currentArticle.content)
                putExtra("ARTICLE_IMAGE_URL", currentArticle.imageUrl)
                putExtra("ARTICLE_TIMESTAMP", currentArticle.createdAt)
                putExtra("ARTICLE_AUTHOR", currentArticle.authorName)
                putExtra("ARTICLE_ID", currentArticle.id)
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