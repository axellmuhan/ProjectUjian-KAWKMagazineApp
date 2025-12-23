package id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp // Sesuaikan package

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast // 👈 Tambahan Import
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth // 👈 Tambahan Import
import java.text.SimpleDateFormat
import java.util.Locale

class DiscussionAdapter(
    private var articleList: List<Article>,
    private val context: Context
) : RecyclerView.Adapter<DiscussionAdapter.DiscussionViewHolder>() {

    // 👇 1. Inisialisasi Auth untuk cek status login
    private val auth = FirebaseAuth.getInstance()

    class DiscussionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgThumb: ImageView = view.findViewById(R.id.img_discussion_thumb)
        val tvTitle: TextView = view.findViewById(R.id.tv_discussion_title)
        val tvAuthor: TextView = view.findViewById(R.id.tv_discussion_author)
        val tvCommentCount: TextView = view.findViewById(R.id.tv_discussion_comment_count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscussionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_discussion_compact, parent, false)
        return DiscussionViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiscussionViewHolder, position: Int) {
        val article = articleList[position]

        // Set Data Teks
        holder.tvTitle.text = article.title

        val sdf = SimpleDateFormat("dd MMM", Locale.getDefault())
        val dateString = try { sdf.format(article.createdAt.toDate()) } catch (e: Exception) { "" }

        holder.tvAuthor.text = "Oleh: ${article.authorName} • $dateString"
        holder.tvCommentCount.text = "${article.commentCount} Komentar"

        // Load Gambar Kecil
        Glide.with(context)
            .load(article.imageUrl)
            .centerCrop()
            .placeholder(R.drawable.news_placeholder_1)
            .into(holder.imgThumb)

        // KLIK ITEM UTAMA: Buka Detail Artikel
        // (Ini AMAN dibiarkan, karena di halaman Detail sudah ada proteksinya sendiri)
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ArticleDetailActivity::class.java).apply {
                putExtra("ARTICLE_ID", article.id)
                putExtra("ARTICLE_TITLE", article.title)
                putExtra("ARTICLE_CONTENT", article.content)
                putExtra("ARTICLE_IMAGE_URL", article.imageUrl)
                putExtra("ARTICLE_AUTHOR", article.authorName)
            }
            context.startActivity(intent)
        }

        // 👇 2. PROTEKSI KLIK KOMENTAR (Disini perubahannya)
        holder.tvCommentCount.setOnClickListener {
            val user = auth.currentUser

            // CEK: Apakah User itu Tamu?
            if (user == null) {
                // JIKA TAMU -> Tampilkan Pesan & Jangan Buka Popup
                Toast.makeText(context, "Login dulu untuk melihat komentar!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // ⛔ BERHENTI DI SINI
            }

            // JIKA USER -> Baru Buka Popup
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