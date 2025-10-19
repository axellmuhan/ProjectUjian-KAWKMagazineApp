package id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp

import com.google.firebase.Timestamp // <-- Import yang benar untuk waktu

data class Article(
    // ID dokumen tidak perlu dimasukkan di sini, karena akan diambil terpisah
    val title: String = "",
    val authorName: String = "",
    val authorId: String = "",
    val imageUrl: String = "",
    val content: String = "",
    val categoryId: String = "",
    val categoryName: String = "",
    val createdAt: Timestamp = Timestamp.now(), // <-- Tipe dan default value yang benar
    val updatedAt: Timestamp = Timestamp.now(), // <-- Tipe dan default value yang benar
    val isPublished: Boolean = false,           // <-- Default value boolean
    val isFeatured: Boolean = false,            // <-- Field yang benar (bukan isPublished)
    val likeCount: Int = 0,                     // <-- Field yang benar, tipe Int, default 0
    val commentCount: Int = 0,                  // <-- Tipe Int, default 0
    val tags: List<String> = emptyList()        // <-- Tipe List<String> untuk array
)