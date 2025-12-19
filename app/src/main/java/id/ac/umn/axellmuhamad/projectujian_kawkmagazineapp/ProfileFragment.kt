package id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp.model.User

class ProfileFragment : Fragment() {

    // Deklarasi variabel UI
    private lateinit var profileImage: ImageView
    private lateinit var profileName: TextView
    private lateinit var profileEmail: TextView
    private lateinit var profileBio: TextView
    private lateinit var logoutButton: Button
    private lateinit var editProfileButton: Button
    private lateinit var backArrow: ImageView

    // Deklarasi Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate layout fragment_profile
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Inisialisasi Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // 2. Hubungkan variabel dengan komponen di layout XML
        // (Pastikan ID di XML sudah sesuai, contoh: @id/profile_image)
        profileImage = view.findViewById(R.id.profile_image)
        profileName = view.findViewById(R.id.profile_name)
        // profileEmail biasanya tidak ada di desain awal, tapi jika ada tambahkan:
        // profileEmail = view.findViewById(R.id.profile_email)
        profileBio = view.findViewById(R.id.profile_bio)
        logoutButton = view.findViewById(R.id.logout_button)
        editProfileButton = view.findViewById(R.id.edit_profile_button)
        backArrow = view.findViewById(R.id.back_arrow)

        // 3. Muat data pengguna dari Firestore
        loadUserProfile()

        // 4. Setup Tombol-tombol
        setupButtons()
    }

    private fun loadUserProfile() {
        // Ambil user yang sedang login saat ini
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val uid = currentUser.uid

            // Ambil dokumen dari koleksi 'users' berdasarkan UID
            firestore.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        // Ubah dokumen Firestore menjadi objek User
                        val user = document.toObject(User::class.java)

                        if (user != null) {
                            // Tampilkan data ke UI
                            profileName.text = user.displayName
                            profileBio.text = user.bio
                            // Jika ada TextView email: profileEmail.text = user.email

                            // Muat gambar profil menggunakan Glide
                            // Cek apakah ada URL gambar, jika tidak pakai default
                            if (user.profileImageUrl.isNotEmpty()) {
                                Glide.with(this)
                                    .load(user.profileImageUrl)
                                    .placeholder(R.drawable.ic_profile_dummy) // Gambar loading
                                    .error(R.drawable.ic_profile_dummy)       // Gambar jika error
                                    .circleCrop() // Agar gambar bulat
                                    .into(profileImage)
                            }
                        }
                    } else {
                        Toast.makeText(context, "Data profil tidak ditemukan.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("ProfileFragment", "Error loading profile", e)
                    Toast.makeText(context, "Gagal memuat profil: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            // Jika user ternyata belum login (session habis), lempar ke login
            Toast.makeText(context, "Sesi habis, silakan login kembali.", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.loginFragment)
        }
    }

    private fun setupButtons() {
        // Tombol Logout dengan Konfirmasi
        logoutButton.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Konfirmasi Logout")
                .setMessage("Apakah Anda yakin ingin keluar dari akun?")
                .setPositiveButton("Ya") { _, _ ->
                    auth.signOut()
                    Toast.makeText(context, "Berhasil Logout.", Toast.LENGTH_SHORT).show()
                    // Arahkan kembali ke halaman Login
                    // Pastikan ID ini sesuai dengan nav_graph.xml Anda
                    findNavController().navigate(R.id.loginFragment)
                }
                .setNegativeButton("Batal", null)
                .show()
        }

        // Tombol Edit Profile (Nanti bisa diarahkan ke fragment edit)
        editProfileButton.setOnClickListener {
            Toast.makeText(context, "Fitur Edit Profil akan segera hadir!", Toast.LENGTH_SHORT).show()
            // findNavController().navigate(R.id.action_profile_to_editProfile) // Contoh navigasi nanti
        }

        // Tombol Kembali
        backArrow.setOnClickListener {
            findNavController().popBackStack()
        }

        // Listener untuk menu dummy lainnya (opsional)
        view?.findViewById<TextView>(R.id.my_articles_option)?.setOnClickListener {
            Toast.makeText(context, "My Articles diklik", Toast.LENGTH_SHORT).show()
        }
    }
}