package id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp // Sesuaikan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CommentBottomSheetFragment : BottomSheetDialogFragment() {

    // ## DEKLARASIKAN ADAPTER DAN LIST DI SINI AGAR BISA DIAKSES DARI MANA SAJA ##
    private lateinit var commentAdapter: CommentAdapter
    private val commentList = mutableListOf<Comment>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_comment_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ## Inisialisasi komponen-komponen ##
        val commentsRecyclerView: RecyclerView = view.findViewById(R.id.comments_recyclerview)
        val postButton: TextView = view.findViewById(R.id.post_button)
        val replyEditText: EditText = view.findViewById(R.id.reply_edittext)
        val cancelButton: TextView = view.findViewById(R.id.cancel_button)

        // Siapkan data komentar awal
        setupInitialComments()

        // Atur RecyclerView
        commentAdapter = CommentAdapter(commentList)
        commentsRecyclerView.layoutManager = LinearLayoutManager(context)
        commentsRecyclerView.adapter = commentAdapter

        // ## Logika untuk tombol Cancel (sudah ada) ##
        cancelButton.setOnClickListener {
            dismiss()
        }

        // ## LOGIKA BARU UNTUK TOMBOL POST ##
        postButton.setOnClickListener {
            val newCommentText = replyEditText.text.toString()

            // Pastikan komentar tidak kosong
            if (newCommentText.isNotBlank()) {
                // Buat objek Comment baru
                val newComment = Comment("You", newCommentText, R.drawable.ic_profile)

                // Tambahkan komentar baru ke dalam list
                commentList.add(newComment)

                // Beri tahu adapter bahwa ada item baru yang ditambahkan di posisi terakhir
                commentAdapter.notifyItemInserted(commentList.size - 1)

                // Scroll ke komentar yang baru ditambahkan
                commentsRecyclerView.scrollToPosition(commentList.size - 1)

                // Kosongkan kolom input
                replyEditText.text.clear()
            }
        }
    }

    // Fungsi untuk mengisi data awal
    private fun setupInitialComments() {
        commentList.clear() // Kosongkan list dulu untuk mencegah duplikasi
        commentList.addAll(listOf(
            Comment("Karrell Wilson", "Sangat informatif!", R.drawable.ic_profile),
            Comment("Ece", "Artikel yang bagus, terima kasih.", R.drawable.ic_profile),
            Comment("John Doe", "Saya setuju dengan poin-poinnya.", R.drawable.ic_profile)
        ))
    }
}