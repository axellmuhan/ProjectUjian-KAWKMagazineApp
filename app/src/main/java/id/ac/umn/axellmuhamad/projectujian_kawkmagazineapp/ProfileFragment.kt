package id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
// import com.bumptech.glide.Glide // Tidak perlu Glide untuk gambar dummy

class ProfileFragment : Fragment() {

    private lateinit var profileImage: ImageView
    private lateinit var profileName: TextView
    private lateinit var profileBio: TextView
    private lateinit var logoutButton: Button
    private lateinit var backArrow: ImageView // Untuk tombol kembali

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Hubungkan view dari layout
        profileImage = view.findViewById(R.id.profile_image)
        profileName = view.findViewById(R.id.profile_name)
        profileBio = view.findViewById(R.id.profile_bio)
        logoutButton = view.findViewById(R.id.logout_button)
        backArrow = view.findViewById(R.id.back_arrow)

        // Muat data dummy pengguna
        loadDummyUserProfile()

        // Atur listener untuk tombol logout (masih dummy)
        logoutButton.setOnClickListener {
            // Untuk sementara, hanya tampilkan pesan Toast
            Toast.makeText(context, "Anda telah logout (Dummy).", Toast.LENGTH_SHORT).show()
            // Jika ada halaman login nanti, bisa dinavigasikan ke sana
            // findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
        }

        // Listener untuk tombol kembali
        backArrow.setOnClickListener {
            findNavController().popBackStack() // Kembali ke fragmen sebelumnya
        }

        // Listener untuk opsi menu dummy
        view.findViewById<TextView>(R.id.my_articles_option).setOnClickListener {
            Toast.makeText(context, "My Articles (Dummy)", Toast.LENGTH_SHORT).show()
        }
        view.findViewById<TextView>(R.id.saved_items_option).setOnClickListener {
            Toast.makeText(context, "Saved Items (Dummy)", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadDummyUserProfile() {
        profileName.text = "Axell Muhamad"
        profileBio.text = "Student at UMN | Android Dev Enthusiast | Exploring new tech."
        // Gambar profil dummy sudah diatur langsung di XML via tools:src
        // Jika ingin memuat dari URL, Anda akan butuh Glide dan URL dummy
        // Glide.with(this).load("URL_GAMBAR_DUMMY").circleCrop().into(profileImage)
    }
}