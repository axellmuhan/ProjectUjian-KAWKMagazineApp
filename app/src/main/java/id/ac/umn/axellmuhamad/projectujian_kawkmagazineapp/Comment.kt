package id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp // Sesuaikan

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude

data class Comment(
    @get:Exclude var id: String = "", // ## TAMBAHKAN BARIS INI ##
    val authorName: String = "",
    val commentText: String = "",
    val createdAt: Timestamp = Timestamp.now()
)