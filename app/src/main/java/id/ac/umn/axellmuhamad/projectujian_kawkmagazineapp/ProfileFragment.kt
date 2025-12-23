package id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp.model.User

class ProfileFragment : Fragment() {

    private lateinit var profileImage: ImageView
    private lateinit var profileName: TextView
    private lateinit var profileBio: TextView
    private lateinit var logoutButton: Button
    private lateinit var editProfileButton: Button

    // Tambahan variabel untuk menu lain agar bisa disembunyikan
    private var savedItemsOption: View? = null

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Inisialisasi Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // 2. Binding Views
        profileImage = view.findViewById(R.id.profile_image)
        profileName = view.findViewById(R.id.profile_name)
        profileBio = view.findViewById(R.id.profile_bio)
        logoutButton = view.findViewById(R.id.logout_button)
        editProfileButton = view.findViewById(R.id.edit_profile_button)
        savedItemsOption = view.findViewById(R.id.saved_items_option)
        val backArrow = view.findViewById<ImageView>(R.id.back_arrow)

        // 3. Cek Status Login (Guest vs User)
        val currentUser = auth.currentUser

        if (currentUser == null) {
            // === JIKA TAMU (GUEST) ===
            setupGuestMode()
        } else {
            // === JIKA USER LOGIN ===
            setupUserMode(currentUser.uid)
        }

        // Tombol Back (Berlaku untuk keduanya)
        backArrow?.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    // --- LOGIKA UNTUK TAMU ---
    private fun setupGuestMode() {
        // Tampilkan Identitas Tamu
        profileName.text = "Pengunjung (Tamu)"
        profileBio.text = "Mode Tamu. Silakan login untuk fitur penuh."
        profileImage.setImageResource(R.drawable.ic_profile_placeholder) // Pastikan ada drawable default

        // Sembunyikan Fitur Khusus User
        editProfileButton.visibility = View.GONE
        savedItemsOption?.visibility = View.GONE
        // (Sembunyikan menu lain seperti My Articles jika ada)

        // Ubah Tombol Logout menjadi Login
        logoutButton.text = "LOGIN SEKARANG"
        logoutButton.setOnClickListener {
            // Arahkan ke Login Fragment
            findNavController().navigate(R.id.loginFragment)
        }
    }

    // --- LOGIKA UNTUK USER ASLI ---
    private fun setupUserMode(userId: String) {
        // Tampilkan Fitur User
        editProfileButton.visibility = View.VISIBLE
        savedItemsOption?.visibility = View.VISIBLE

        logoutButton.text = "LOGOUT"

        // Load Data Profil dari Firestore (Real-time)
        loadUserProfile(userId)

        // Setup Klik Tombol User
        editProfileButton.setOnClickListener {
            findNavController().navigate(R.id.editProfileFragment)
        }

        savedItemsOption?.setOnClickListener {
            val intent = Intent(requireContext(), SavedArticlesActivity::class.java)
            startActivity(intent)
        }

        logoutButton.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Yakin ingin keluar?")
                .setPositiveButton("Ya") { _, _ ->
                    auth.signOut()
                    // Kembali ke Login
                    findNavController().navigate(R.id.loginFragment)
                }
                .setNegativeButton("Batal", null)
                .show()
        }
    }

    private fun loadUserProfile(userId: String) {
        // Menggunakan SnapshotListener agar UI update otomatis jika edit profile berhasil
        firestore.collection("users").document(userId)
            .addSnapshotListener { document, error ->
                if (error != null) {
                    Log.e("ProfileFragment", "Listen failed.", error)
                    return@addSnapshotListener
                }

                if (document != null && document.exists()) {
                    val user = document.toObject(User::class.java)
                    if (user != null) {
                        profileName.text = user.displayName ?: "User Tanpa Nama"
                        profileBio.text = user.bio ?: "-"

                        if (user.profileImageUrl.isNotEmpty()) {
                            try {
                                Glide.with(this)
                                    .load(user.profileImageUrl)
                                    .placeholder(R.drawable.ic_profile_placeholder)
                                    .circleCrop()
                                    .into(profileImage)
                            } catch (e: Exception) {
                                Log.e("ProfileFragment", "Glide error", e)
                            }
                        }
                    }
                }
            }
    }
}