package id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp // Sesuaikan dengan package Anda

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Cari NavHostFragment (area untuk menampilkan halaman)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // 2. Cari BottomNavigationView dari layout
        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // 3. (BAGIAN PENTING) Hubungkan NavController dengan BottomNavigationView
        // Perintah ini membuat tombol navigasi bisa mengganti halaman.
        bottomNavView.setupWithNavController(navController)
    }
}