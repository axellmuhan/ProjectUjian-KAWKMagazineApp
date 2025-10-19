package id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp // Sesuaikan

data class NewsPost(
    val title: String = "",
    val content: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val imageUrl: String = "",
    val categoryId: String = "",
    val categoryName: String = "",
    val isPublished: Boolean = true,
    val isFeatured: Boolean = true,
    val likeCount: Long = 0, // Gunakan Long untuk tipe 'number'
    val commentCount: Long = 0,
    val tags: List<String> = listOf() // 'array' di Firestore menjadi 'List' di Kotlin
)