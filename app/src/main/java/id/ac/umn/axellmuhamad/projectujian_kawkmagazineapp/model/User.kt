package id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp.model

data class User(
    val displayName: String = "",
    val email: String = "",
    val bio: String = "",
    val profileImageUrl: String = "",
    val role: String = "reader"
)