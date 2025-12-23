package id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class CommentBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var commentAdapter: CommentAdapter
    private val db = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth // [UBAH 2] Variabel Auth
    private var articleId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            articleId = it.getString(ARG_ARTICLE_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_comment_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // [UBAH 3] Inisialisasi Auth
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        val commentsRecyclerView: RecyclerView = view.findViewById(R.id.comments_recyclerview)
        val postButton: TextView = view.findViewById(R.id.post_button)
        val replyEditText: EditText = view.findViewById(R.id.reply_edittext)
        val cancelButton: TextView = view.findViewById(R.id.cancel_button)

        // Setup Adapter
        commentAdapter = CommentAdapter(mutableListOf(), articleId ?: "", requireContext())
        commentsRecyclerView.layoutManager = LinearLayoutManager(context)
        commentsRecyclerView.adapter = commentAdapter

        // Load Komentar (Semua orang boleh baca)
        fetchComments()

        cancelButton.setOnClickListener {
            dismiss()
        }

        // ===========================================
        // 🔒 [UBAH 4] LOGIKA GUEST MODE (PENTING!)
        // ===========================================
        if (currentUser == null) {
            // --- JIKA TAMU (GUEST) ---

            // 1. Sembunyikan kolom ketik biar ga bisa ngetik
            replyEditText.visibility = View.GONE

            // 2. Ubah tombol "POST" jadi tombol "LOGIN"
            postButton.text = "LOGIN"

            // 3. Arahkan ke halaman Login saat diklik
            postButton.setOnClickListener {
                dismiss() // Tutup popup dulu

                // GANTI KE MainActivity
                val intent = Intent(requireContext(), MainActivity::class.java)
                // Flag ini untuk menghapus history agar user tidak bisa tekan Back ke halaman sebelumnya
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }

        } else {
            // --- JIKA USER (SUDAH LOGIN) ---

            // Tampilan normal
            replyEditText.visibility = View.VISIBLE
            postButton.text = "POST"

            // Logika Posting Normal
            postButton.setOnClickListener {
                val commentText = replyEditText.text.toString()
                if (commentText.isNotBlank() && articleId != null) {
                    postNewComment(commentText, replyEditText)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Menutup resource ML saat ditutup (Penting agar tidak memory leak)
        if (::commentAdapter.isInitialized) {
            commentAdapter.releaseResources()
        }
    }

    private fun fetchComments() {
        if (articleId == null) return

        db.collection("articles").document(articleId!!)
            .collection("comments")
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.e("CommentSheet", "Error fetching comments", error)
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    val fetchedComments = mutableListOf<Comment>()
                    for (document in snapshots.documents) {
                        val comment = document.toObject(Comment::class.java)
                        if (comment != null) {
                            comment.id = document.id
                            fetchedComments.add(comment)
                        }
                    }
                    commentAdapter.setData(fetchedComments)
                }
            }
    }

    private fun postNewComment(commentText: String, replyEditText: EditText) {
        val articleDocRef = db.collection("articles").document(articleId!!)

        // [UBAH 5] Ambil nama asli user, jangan hardcode "You" lagi
        val user = auth.currentUser
        val realName = user?.displayName ?: user?.email ?: "Anonymous"

        val newComment = Comment(
            authorName = realName,
            commentText = commentText
            // Pastikan field lain seperti timestamp di-handle di Model atau ServerTimestamp
        )

        db.runBatch { batch ->
            val newCommentRef = articleDocRef.collection("comments").document()
            batch.set(newCommentRef, newComment)
            batch.update(articleDocRef, "commentCount", FieldValue.increment(1))
        }
            .addOnSuccessListener {
                Toast.makeText(context, "Comment posted!", Toast.LENGTH_SHORT).show()
                replyEditText.text.clear()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to post: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("CommentSheet", "Error posting comment", e)
            }
    }

    companion object {
        private const val ARG_ARTICLE_ID = "article_id"

        fun newInstance(articleId: String): CommentBottomSheetFragment {
            val fragment = CommentBottomSheetFragment()
            val args = Bundle()
            args.putString(ARG_ARTICLE_ID, articleId)
            fragment.arguments = args
            return fragment
        }
    }
}