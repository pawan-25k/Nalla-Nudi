package com.nudi.nallanudi.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.nudi.nallanudi.data.WordDatabase
import com.nudi.nallanudi.data.WordRepository
import com.nudi.nallanudi.databinding.ActivitySplashBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dao = WordDatabase.getDatabase(this).wordDao()
        val repository = WordRepository(dao)

        lifecycleScope.launch {
            // Ensure DB is seeded
            repository.seedDatabase(this@SplashActivity)
            // Small delay for branding
            delay(1500)
            // Go to login
            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            finish()
        }
    }
}
