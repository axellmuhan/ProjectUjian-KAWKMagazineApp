package id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    // Durasi splash screen dalam milidetik (misal: 2500ms = 2.5 detik)
    private val SPLASH_TIME_OUT: Long = 2500

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Handler untuk menjalankan sebuah aksi setelah jeda waktu tertentu
        Handler(Looper.getMainLooper()).postDelayed({
            // Kode ini akan dijalankan setelah SPLASH_TIME_OUT

            // Membuat Intent untuk pindah ke MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

            // Menutup SplashActivity agar tidak bisa kembali dengan tombol back
            finish()
        }, SPLASH_TIME_OUT)
    }
}