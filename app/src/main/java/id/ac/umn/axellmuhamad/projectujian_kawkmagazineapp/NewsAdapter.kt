package id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp // Sesuaikan

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Adapter sekarang menerima sebuah daftar (List) dari NewsPost
class NewsAdapter(private val newsList: List<NewsPost>) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    // ViewHolder sekarang memegang referensi ke setiap view di dalam item
    class NewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val authorNameTextView: TextView = view.findViewById(R.id.author_name)
        val postTextView: TextView = view.findViewById(R.id.post_text)
        val postImageView: ImageView = view.findViewById(R.id.post_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post_dark, parent, false)
        return NewsViewHolder(view)
    }

    // Fungsi ini menghubungkan data dari list ke view
    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val currentPost = newsList[position]

        // Isi data ke dalam view
        holder.authorNameTextView.text = currentPost.authorName
        holder.postTextView.text = currentPost.postText
        holder.postImageView.setImageResource(currentPost.imageResId)

        // **BAGIAN TERPENTING: Menambahkan OnClickListener**
        holder.postImageView.setOnClickListener {
            val context = holder.itemView.context
            // Buat Intent untuk membuka ImageViewerActivity
            val intent = Intent(context, ImageViewerActivity::class.java).apply {
                // Kirim ID gambar yang diklik ke activity baru
                putExtra("IMAGE_RES_ID", currentPost.imageResId)
            }
            context.startActivity(intent)
        }
    }

    // Ukuran daftar sekarang dinamis berdasarkan data yang diberikan
    override fun getItemCount(): Int {
        return newsList.size
    }
}