package id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

class NewsAdapter(private val newsList: List<NewsPost>) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    class NewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val authorNameTextView: TextView = view.findViewById(R.id.author_name)
        val postTextView: TextView = view.findViewById(R.id.post_text)
        val postImageView: ImageView = view.findViewById(R.id.post_image)
        val commentActionView: TextView = view.findViewById(R.id.comment_action)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post_dark, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val currentPost = newsList[position]

        holder.authorNameTextView.text = currentPost.authorName
        holder.postTextView.text = currentPost.postText
        holder.postImageView.setImageResource(currentPost.imageResId)

        holder.postImageView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ImageViewerActivity::class.java).apply {
                putExtra("IMAGE_RES_ID", currentPost.imageResId)
            }
            context.startActivity(intent)
        }

        holder.commentActionView.setOnClickListener {
            val context = holder.itemView.context
            val fragmentManager = (context as? AppCompatActivity)?.supportFragmentManager

            if (fragmentManager != null) {
                val commentSheet = CommentBottomSheetFragment()
                commentSheet.show(fragmentManager, "CommentBottomSheetFragment")
            }
        }
    }

    override fun getItemCount(): Int {
        return newsList.size
    }
}