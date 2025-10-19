package id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Hubungkan BottomNavigationView dengan NavController agar bisa berpindah halaman
        bottomNavigationView.setupWithNavController(navController)

        // === BAGIAN KUNCINYA ADA DI SINI ===
        // Tambahkan listener untuk mendeteksi setiap perubahan halaman
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                // Jika halaman saat ini adalah login atau register...
                R.id.loginFragment, R.id.registerFragment -> {
                    // ...sembunyikan menu bawah.
                    bottomNavigationView.visibility = View.GONE
                }
                // Jika halaman lain...
                else -> {
                    // ...tampilkan kembali menu bawah.
                    bottomNavigationView.visibility = View.VISIBLE
                }
            }
        }
    }
}