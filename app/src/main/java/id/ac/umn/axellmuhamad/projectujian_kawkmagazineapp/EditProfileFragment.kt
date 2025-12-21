package id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp.model.User

class EditProfileFragment : Fragment() {

    private lateinit var etName: EditText
    private lateinit var etBio: EditText
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etName = view.findViewById(R.id.et_edit_name)
        etBio = view.findViewById(R.id.et_edit_bio)
        val btnSave = view.findViewById<Button>(R.id.btn_save_profile)
        val btnBack = view.findViewById<ImageView>(R.id.btn_back_edit)

        // 1. Ambil data user saat ini dari Firestore agar tidak kosong
        val uid = auth.currentUser?.uid
        if (uid != null) {
            db.collection("users").document(uid).get().addOnSuccessListener { doc ->
                val user = doc.toObject(User::class.java)
                etName.setText(user?.displayName)
                etBio.setText(user?.bio)
            }
        }

        btnBack.setOnClickListener { findNavController().popBackStack() }

        // 2. Simpan perubahan ke Firestore
        btnSave.setOnClickListener {
            val updatedName = etName.text.toString().trim()
            val updatedBio = etBio.text.toString().trim()

            if (updatedName.isEmpty()) {
                Toast.makeText(context, "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (uid != null) {
                val userUpdates = mapOf(
                    "displayName" to updatedName,
                    "bio" to updatedBio
                )
                db.collection("users").document(uid).update(userUpdates)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Profil Berhasil Diperbarui!", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack() // Kembali ke ProfileFragment
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Gagal memperbarui profil", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}