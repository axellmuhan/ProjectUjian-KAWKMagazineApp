package id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp // Sesuaikan

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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class CommentBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var commentAdapter: CommentAdapter
    private val commentList = mutableListOf<Comment>()
    private val db = FirebaseFirestore.getInstance()
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

        val commentsRecyclerView: RecyclerView = view.findViewById(R.id.comments_recyclerview)
        val postButton: TextView = view.findViewById(R.id.post_button)
        val replyEditText: EditText = view.findViewById(R.id.reply_edittext)
        val cancelButton: TextView = view.findViewById(R.id.cancel_button)

        // Setup RecyclerView
        commentAdapter = CommentAdapter(commentList)
        commentsRecyclerView.layoutManager = LinearLayoutManager(context)
        commentsRecyclerView.adapter = commentAdapter

        // Mulai ambil data komentar dari Firestore
        // Jika tidak ada koneksi/data, data contoh akan ditampilkan
        setupInitialComments()
        fetchComments()

        cancelButton.setOnClickListener {
            dismiss()
        }

        postButton.setOnClickListener {
            val commentText = replyEditText.text.toString()
            if (commentText.isNotBlank() && articleId != null) {
                postNewComment(commentText, replyEditText)
            }
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
                if (snapshots != null && !snapshots.isEmpty) {
                    val fetchedComments = snapshots.toObjects(Comment::class.java)
                    commentList.clear()
                    commentList.addAll(fetchedComments)
                    commentAdapter.notifyDataSetChanged()
                }
            }
    }

    private fun postNewComment(commentText: String, replyEditText: EditText) {
        // Kita ubah data class yang dikirim ke Firestore agar sesuai
        val newCommentData = hashMapOf(
            "authorName" to "You",
            "commentText" to commentText,
            "createdAt" to com.google.firebase.Timestamp.now()
        )

        db.collection("articles").document(articleId!!)
            .collection("comments")
            .add(newCommentData)
            .addOnSuccessListener {
                Toast.makeText(context, "Comment posted!", Toast.LENGTH_SHORT).show()
                replyEditText.text.clear()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to post comment: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("CommentSheet", "Error posting comment", e)
            }
    }

    // ## BAGIAN YANG DIPERBAIKI ##
    private fun setupInitialComments() {
        commentList.clear()
        // Tambahkan parameter ketiga (R.drawable.ic_profile) untuk setiap item
        commentList.addAll(listOf(
            Comment("Karrell Wilson", "Sangat informatif!", R.drawable.ic_profile),
            Comment("Ece", "Artikel yang bagus, terima kasih.", R.drawable.ic_profile),
            Comment("John Doe", "Saya setuju dengan poin-poinnya.", R.drawable.ic_profile)
        ))
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