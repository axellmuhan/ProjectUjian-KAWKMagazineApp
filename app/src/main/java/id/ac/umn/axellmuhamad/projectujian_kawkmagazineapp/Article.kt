package id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp

import com.google.firebase.Timestamp

data class Article(
    var id: String = "", // ## TAMBAHKAN BARIS INI ##
    val title: String = "",
    val authorName: String = "",
    val authorId: String = "",
    val imageUrl: String = "",
    val content: String = "",
    val categoryId: String = "",
    val categoryName: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now(),
    val isPublished: Boolean = false,
    val isFeatured: Boolean = false,
    var likeCount: Long = 0,    // Diubah menjadi var agar bisa diupdate
    val commentCount: Long = 0,
    val tags: List<String> = emptyList()
)