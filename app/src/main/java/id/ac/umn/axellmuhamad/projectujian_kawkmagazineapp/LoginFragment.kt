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
import android.widget.TextView

class LoginFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi Firebase Auth
        auth = FirebaseAuth.getInstance()

        val emailEditText = view.findViewById<EditText>(R.id.login_email)
        val passwordEditText = view.findViewById<EditText>(R.id.login_password)
        val loginButton = view.findViewById<Button>(R.id.login_button)
        // Di dalam onViewCreated di LoginFragment.kt
        val goToRegisterText = view.findViewById<TextView>(R.id.go_to_register_text)
        goToRegisterText.setOnClickListener {
            // Gunakan action yang sudah kita buat di nav_graph
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            // Validasi input
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Email dan Password tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Proses login dengan Firebase
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    // Jika login berhasil
                    Toast.makeText(context, "Login Berhasil!", Toast.LENGTH_SHORT).show()
                    // Arahkan ke halaman utama (HomeFragment)
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                }
                .addOnFailureListener { e ->
                    // Jika login gagal
                    Toast.makeText(context, "Login Gagal: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}