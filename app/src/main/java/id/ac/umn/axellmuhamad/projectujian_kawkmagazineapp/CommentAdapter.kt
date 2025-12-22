package id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class CommentAdapter(
    private var comments: List<Comment>,
    private val articleId: String,
    private val context: Context
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    // Ubah jadi Nullable (?) supaya kalau error bisa di-isi null
    private var sentimentHelper: SentimentHelper? = null

    init {
        // --- PENGAMAN ANTI CRASH ---
        try {
            sentimentHelper = SentimentHelper(context)
        } catch (e: Exception) {
            Log.e("CommentAdapter", "FATAL: Gagal memuat ML! Aplikasi tetap jalan tanpa label.", e)
            sentimentHelper = null // Nonaktifkan ML jika gagal load
        }
    }

    class CommentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.commenter_name)
        val text: TextView = view.findViewById(R.id.comment_text)
        val image: ImageView = view.findViewById(R.id.commenter_image)
        val timestamp: TextView = view.findViewById(R.id.comment_timestamp)
        val sentimentBadge: TextView = view.findViewById(R.id.tv_sentiment_badge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]

        holder.name.text = comment.authorName
        holder.text.text = comment.commentText
        // Gunakan formatTimeAgo jika fungsinya ada, jika tidak pakai string biasa dulu
        holder.timestamp.text = "• Baru saja"
        holder.image.setImageResource(R.drawable.ic_profile)

        // --- LOGIKA ML DENGAN PENGAMAN ---
        val textToAnalyze = comment.commentText

        // Cek: Apakah helper berhasil dimuat DAN teks tidak kosong?
        if (sentimentHelper != null && textToAnalyze.isNotEmpty()) {
            try {
                val result = sentimentHelper!!.predict(textToAnalyze)

                holder.sentimentBadge.text = result
                holder.sentimentBadge.visibility = View.VISIBLE

                when {
                    result.contains("Positif") -> holder.sentimentBadge.setBackgroundColor(Color.parseColor("#2E7D32"))
                    result.contains("Negatif") -> holder.sentimentBadge.setBackgroundColor(Color.parseColor("#C62828"))
                    else -> holder.sentimentBadge.setBackgroundColor(Color.parseColor("#455A64"))
                }
            } catch (e: Exception) {
                // Kalau prediksi gagal, sembunyikan badge saja, jangan crash
                Log.e("CommentAdapter", "Gagal prediksi: ${e.message}")
                holder.sentimentBadge.visibility = View.GONE
            }
        } else {
            // Kalau ML mati atau teks kosong
            holder.sentimentBadge.visibility = View.GONE
        }

        // --- FITUR HAPUS ---
        holder.itemView.setOnLongClickListener {
            AlertDialog.Builder(context)
                .setTitle("Hapus Komentar")
                .setMessage("Apakah Anda yakin ingin menghapus komentar ini?")
                .setPositiveButton("Hapus") { dialog, _ ->
                    deleteComment(comment.id)
                    dialog.dismiss()
                }
                .setNegativeButton("Batal", null)
                .show()
            true
        }
    }

    override fun getItemCount() = comments.size

    fun setData(newList: List<Comment>) {
        comments = newList
        notifyDataSetChanged()
    }

    fun releaseResources() {
        try {
            sentimentHelper?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun deleteComment(commentId: String) {
        if (articleId.isBlank() || commentId.isBlank()) return

        val db = FirebaseFirestore.getInstance()
        val articleDocRef = db.collection("articles").document(articleId)
        val commentDocRef = articleDocRef.collection("comments").document(commentId)

        db.runBatch { batch ->
            batch.delete(commentDocRef)
            batch.update(articleDocRef, "commentCount", FieldValue.increment(-1))
        }
    }
}