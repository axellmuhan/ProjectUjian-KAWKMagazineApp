package id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp // Sesuaikan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CommentBottomSheetFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_comment_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val commentsRecyclerView: RecyclerView = view.findViewById(R.id.comments_recyclerview)
        commentsRecyclerView.layoutManager = LinearLayoutManager(context)

        // Buat data komentar contoh
        val dummyComments = listOf(
            Comment("Karrell Wilson", "Sangat informatif!", R.drawable.ic_profile),
            Comment("Ece", "Artikel yang bagus, terima kasih.", R.drawable.ic_profile),
            Comment("John Doe", "Saya setuju dengan poin-poinnya.", R.drawable.ic_profile)
        )

        commentsRecyclerView.adapter = CommentAdapter(dummyComments)
    }
}