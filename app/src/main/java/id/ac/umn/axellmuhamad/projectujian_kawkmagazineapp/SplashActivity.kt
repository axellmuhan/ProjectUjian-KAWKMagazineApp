package id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp // Pastikan ini sesuai dengan package Anda

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    // Durasi splash screen dalam milidetik (2.5 detik)
    private val SPLASH_TIME_OUT: Long = 2500

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Handler ini akan menjalankan kode di dalamnya setelah jeda waktu
        Handler(Looper.getMainLooper()).postDelayed({

            // 1. Perintah untuk pindah ke MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

            // 2. (SANGAT PENTING) Perintah untuk menutup SplashActivity
            finish()

        }, SPLASH_TIME_OUT)
    }
}