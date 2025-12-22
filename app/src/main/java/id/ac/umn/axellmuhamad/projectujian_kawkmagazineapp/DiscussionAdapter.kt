package id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp // Sesuaikan package

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Locale

class DiscussionAdapter(
    private var articleList: List<Article>,
    private val context: Context
) : RecyclerView.Adapter<DiscussionAdapter.DiscussionViewHolder>() {

    class DiscussionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgThumb: ImageView = view.findViewById(R.id.img_discussion_thumb)
        val tvTitle: TextView = view.findViewById(R.id.tv_discussion_title)
        val tvAuthor: TextView = view.findViewById(R.id.tv_discussion_author)
        val tvCommentCount: TextView = view.findViewById(R.id.tv_discussion_comment_count)
        // val tvTime: TextView = view.findViewById(R.id.tv_discussion_time) // Bisa digabung ke author biar hemat tempat
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscussionViewHolder {
        // Menggunakan layout compact yang baru dibuat
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_discussion_compact, parent, false)
        return DiscussionViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiscussionViewHolder, position: Int) {
        val article = articleList[position]

        // Set Data Teks
        holder.tvTitle.text = article.title

        // Format tanggal simpel
        val sdf = SimpleDateFormat("dd MMM", Locale.getDefault())
        val dateString = try { sdf.format(article.createdAt.toDate()) } catch (e: Exception) { "" }

        holder.tvAuthor.text = "Oleh: ${article.authorName} • $dateString"
        holder.tvCommentCount.text = "${article.commentCount} Komentar"

        // Load Gambar Kecil (Thumbnail)
        Glide.with(context)
            .load(article.imageUrl)
            .centerCrop() // Agar kotak rapi
            .placeholder(R.drawable.news_placeholder_1) // Pastikan drawable ini ada
            .into(holder.imgThumb)

        // KLIK UTAMA: Buka Detail Artikel (Agar user bisa baca konteks dulu)
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ArticleDetailActivity::class.java).apply {
                putExtra("ARTICLE_ID", article.id)
                putExtra("ARTICLE_TITLE", article.title)
                putExtra("ARTICLE_CONTENT", article.content)
                putExtra("ARTICLE_IMAGE_URL", article.imageUrl)
                putExtra("ARTICLE_AUTHOR", article.authorName)
                // Kirim timestamp jika perlu (Parcelable)
            }
            context.startActivity(intent)
        }

        // KLIK JUMLAH KOMENTAR: Langsung Buka Popup Komentar (Shortcut)
        holder.tvCommentCount.setOnClickListener {
            if (article.id.isNotEmpty()) {
                val commentSheet = CommentBottomSheetFragment.newInstance(article.id)
                val fragmentManager = (context as AppCompatActivity).supportFragmentManager
                commentSheet.show(fragmentManager, "CommentBottomSheet")
            }
        }
    }

    override fun getItemCount() = articleList.size

    fun setData(newList: List<Article>) {
        articleList = newList
        notifyDataSetChanged()
    }
}