package id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp // Sesuaikan package

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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class CommentBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var commentAdapter: CommentAdapter
    // Kita tidak butuh list lokal 'commentList' lagi di sini karena adapter sudah menanganinya
    // private val commentList = mutableListOf<Comment>()

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

        // [UBAH 1] Tambahkan requireContext() di parameter ke-3
        // Parameter: (List Kosong Awal, ID Artikel, Context)
        commentAdapter = CommentAdapter(mutableListOf(), articleId ?: "", requireContext())

        commentsRecyclerView.layoutManager = LinearLayoutManager(context)
        commentsRecyclerView.adapter = commentAdapter

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

    // [UBAH 2] Tambahkan ini untuk menutup Model ML saat sheet ditutup
    override fun onDestroyView() {
        super.onDestroyView()
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

                    // [UBAH 3] Pakai setData() milik adapter biar lebih bersih
                    commentAdapter.setData(fetchedComments)
                }
            }
    }

    private fun postNewComment(commentText: String, replyEditText: EditText) {
        val articleDocRef = db.collection("articles").document(articleId!!)

        // Sesuaikan dengan Model Comment kamu (tambahkan field createdAt jika perlu di model)
        val newComment = Comment(
            authorName = "You", // Nanti bisa diganti nama user asli dari FirebaseAuth
            commentText = commentText
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
                Toast.makeText(context, "Failed to post comment: ${e.message}", Toast.LENGTH_SHORT).show()
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