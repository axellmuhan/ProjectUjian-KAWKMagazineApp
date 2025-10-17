package id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp

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

        val commentsRecyclerView: RecyclerView = view.findViewById(R.id.comments_recyclerview)
        val postButton: TextView = view.findViewById(R.id.post_button)
        val replyEditText: EditText = view.findViewById(R.id.reply_edittext)
        val cancelButton: TextView = view.findViewById(R.id.cancel_button)

        setupInitialComments()

        commentAdapter = CommentAdapter(commentList)
        commentsRecyclerView.layoutManager = LinearLayoutManager(context)
        commentsRecyclerView.adapter = commentAdapter

        cancelButton.setOnClickListener {
            dismiss()
        }

        postButton.setOnClickListener {
            val newCommentText = replyEditText.text.toString()

            if (newCommentText.isNotBlank()) {
                val newComment = Comment("You", newCommentText, R.drawable.ic_profile)

                commentList.add(newComment)

                commentAdapter.notifyItemInserted(commentList.size - 1)

                commentsRecyclerView.scrollToPosition(commentList.size - 1)

                replyEditText.text.clear()
            }
        }
    }

    private fun setupInitialComments() {
        commentList.clear()
        commentList.addAll(listOf(
            Comment("Karrell Wilson", "Sangat informatif!", R.drawable.ic_profile),
            Comment("Ece", "Artikel yang bagus, terima kasih.", R.drawable.ic_profile),
            Comment("John Doe", "Saya setuju dengan poin-poinnya.", R.drawable.ic_profile)
        ))
    }
}