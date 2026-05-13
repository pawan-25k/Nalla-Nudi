package com.nudi.nallanudi.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.nudi.nallanudi.MainActivity
import com.nudi.nallanudi.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()

            if (email.isBlank()) {
                binding.etEmail.error = getString(com.nudi.nallanudi.R.string.email_required)
                return@setOnClickListener
            }
            if (password.isBlank()) {
                binding.etPassword.error = getString(com.nudi.nallanudi.R.string.password_required)
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        navigateToMain()
                    } else {
                        Toast.makeText(
                            this,
                            task.exception?.localizedMessage
                                ?: getString(com.nudi.nallanudi.R.string.login_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        binding.tvGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            navigateToMain()
        }
    }

    private fun navigateToMain() {
        startActivity(
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        )
        finish()
    }
}
