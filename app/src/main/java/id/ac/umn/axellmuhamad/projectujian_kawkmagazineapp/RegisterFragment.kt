package id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val nameEditText = view.findViewById<EditText>(R.id.register_name)
        val emailEditText = view.findViewById<EditText>(R.id.register_email)
        val passwordEditText = view.findViewById<EditText>(R.id.register_password)
        val registerButton = view.findViewById<Button>(R.id.register_button)

        registerButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Semua kolom harus diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 1. Buat akun di Firebase Authentication
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { authResult ->
                    // Jika berhasil, dapatkan UID pengguna baru
                    val uid = authResult.user?.uid
                    if (uid != null) {
                        // 2. Buat dokumen profil di Firestore
                        val newUser = User(
                            displayName = name,
                            email = email,
                            role = "reader", // Set peran default
                            bio = "Pengguna baru KAWK Magazine App!"
                        )

                        firestore.collection("users").document(uid).set(newUser)
                            .addOnSuccessListener {
                                // Jika profil berhasil disimpan
                                Toast.makeText(context, "Registrasi Berhasil!", Toast.LENGTH_SHORT).show()
                                findNavController().navigate(R.id.homeFragment)
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(context, "Gagal menyimpan profil: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    }
                }
                .addOnFailureListener { e ->
                    // Jika gagal membuat akun
                    Toast.makeText(context, "Registrasi Gagal: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}