package id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView // Pastikan import TextView ada
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth

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

        // Cek dulu, kalau user sebenernya sudah login (dari sesi sebelumnya), langsung masuk aja
        if (auth.currentUser != null) {
            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
            return
        }

        val emailEditText = view.findViewById<EditText>(R.id.login_email)
        val passwordEditText = view.findViewById<EditText>(R.id.login_password)
        val loginButton = view.findViewById<Button>(R.id.login_button)
        val goToRegisterText = view.findViewById<TextView>(R.id.go_to_register_text)

        // ============================================================
        // 👇 FITUR BARU: TOMBOL GUEST MODE 👇
        // ============================================================
        // Pastikan ID 'tv_guest_mode' sudah dibuat di XML langkah 1 tadi
        val guestButton = view.findViewById<TextView>(R.id.tv_guest_mode)

        guestButton.setOnClickListener {
            // Trik Guest Mode: Langsung navigasi ke Home TANPA login Firebase.
            // Akibatnya: auth.currentUser akan bernilai NULL di halaman selanjutnya.
            // Ini akan memicu fitur "Guest" yang sudah kita buat di ArticleDetailActivity.
            Toast.makeText(context, "Masuk sebagai Tamu", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
        }
        // ============================================================


        goToRegisterText.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Email dan Password tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    Toast.makeText(context, "Login Berhasil!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Login Gagal: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}