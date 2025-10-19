package id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp // Sesuaikan

import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class CommentAdapter(private val comments: List<Comment>, private val articleId: String) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    class CommentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.commenter_name)
        val text: TextView = view.findViewById(R.id.comment_text)
        val image: ImageView = view.findViewById(R.id.commenter_image)
        val timestamp: TextView = view.findViewById(R.id.comment_timestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.name.text = comment.authorName
        holder.text.text = comment.commentText
        holder.image.setImageResource(R.drawable.ic_profile)
        holder.timestamp.text = "• ${formatTimeAgo(comment.createdAt)}"

        // ## LOGIKA BARU UNTUK HAPUS DENGAN LONG PRESS ##
        holder.itemView.setOnLongClickListener {
            val context = holder.itemView.context

            // Tampilkan dialog konfirmasi
            AlertDialog.Builder(context)
                .setTitle("Hapus Komentar")
                .setMessage("Apakah Anda yakin ingin menghapus komentar ini?")
                .setPositiveButton("Hapus") { dialog, _ ->
                    // Jika pengguna menekan "Hapus", jalankan fungsi hapus
                    deleteComment(comment.id)
                    dialog.dismiss()
                }
                .setNegativeButton("Batal", null)
                .show()

            true // Mengembalikan true menandakan bahwa kita telah menangani event ini
        }
    }

    override fun getItemCount() = comments.size

    private fun deleteComment(commentId: String) {
        if (articleId.isBlank() || commentId.isBlank()) return

        val db = FirebaseFirestore.getInstance()
        val articleDocRef = db.collection("articles").document(articleId)
        val commentDocRef = articleDocRef.collection("comments").document(commentId)

        // Gunakan batched write untuk menghapus dan mengurangi count sekaligus
        db.runBatch { batch ->
            // Operasi 1: Hapus dokumen komentar
            batch.delete(commentDocRef)
            // Operasi 2: Kurangi nilai 'commentCount' di dokumen artikel utama
            batch.update(articleDocRef, "commentCount", FieldValue.increment(-1))
        }
            .addOnSuccessListener {
                Log.d("CommentDelete", "Komentar berhasil dihapus.")
            }
            .addOnFailureListener { e ->
                Log.e("CommentDelete", "Gagal menghapus komentar.", e)
            }
    }
}