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
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        profileImage = view.findViewById(R.id.profile_image)
        profileName = view.findViewById(R.id.profile_name)
        profileBio = view.findViewById(R.id.profile_bio)
        logoutButton = view.findViewById(R.id.logout_button)
        editProfileButton = view.findViewById(R.id.edit_profile_button)

        loadUserProfile()
        setupButtons(view)
    }

    private fun loadUserProfile() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            firestore.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    val user = document.toObject(User::class.java)
                    if (user != null) {
                        profileName.text = user.displayName
                        profileBio.text = user.bio
                        if (user.profileImageUrl.isNotEmpty()) {
                            Glide.with(this).load(user.profileImageUrl).circleCrop().into(profileImage)
                        }
                    }
                }
        }
    }

    private fun setupButtons(view: View) {
        // [PERBAIKAN DI SINI]
        // Menggunakan <View> alih-alih <LinearLayout> untuk menghindari ClassCastException
        // Karena di XML kamu, ID ini dipasang pada TextView.
        view.findViewById<View>(R.id.saved_items_option)?.setOnClickListener {
            val intent = Intent(requireContext(), SavedArticlesActivity::class.java)
            startActivity(intent)
        }

        logoutButton.setOnClickListener {
            AlertDialog.Builder(requireContext()).setTitle("Logout").setMessage("Yakin keluar?")
                .setPositiveButton("Ya") { _, _ ->
                    auth.signOut()
                    findNavController().navigate(R.id.loginFragment)
                }.setNegativeButton("Batal", null).show()
        }

        // Gunakan safety check (?.) untuk menghindari crash jika ID tidak ditemukan
        view.findViewById<ImageView>(R.id.back_arrow)?.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}