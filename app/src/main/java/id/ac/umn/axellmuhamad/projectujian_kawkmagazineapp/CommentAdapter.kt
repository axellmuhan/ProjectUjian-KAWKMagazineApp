package id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp // Sesuaikan dengan package Anda

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CommentAdapter(private val comments: List<Comment>) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    class CommentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.commenter_name)
        val text: TextView = view.findViewById(R.id.comment_text)
        val image: ImageView = view.findViewById(R.id.commenter_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.name.text = comment.authorName
        holder.text.text = comment.commentText
        holder.image.setImageResource(comment.authorImageResId)
    }

    override fun getItemCount() = comments.size
}