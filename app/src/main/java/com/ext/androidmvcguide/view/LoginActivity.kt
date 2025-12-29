package com.ext.androidmvcguide.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ext.androidmvcguide.R
import com.ext.androidmvcguide.controller.LoginController
import com.ext.androidmvcguide.databinding.ActivityLoginBinding

/**
 * VIEW: Login Screen
 * Implements LoginController.LoginView interface
 * Handles UI updates and user input
 */
class LoginActivity : AppCompatActivity(), LoginController.LoginView {

    private lateinit var binding: ActivityLoginBinding
    private val controller = LoginController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Attach controller to view
        controller.attachView(this)

        setupListeners()
    }

    private fun setupListeners() {
        // Login button click
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            // Clear previous errors
            binding.tilEmail.error = null
            binding.tilPassword.error = null

            // Delegate to controller
            controller.login(email, password)
        }

        // Navigate to register screen
        binding.tvRegisterHere.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Detach controller to prevent memory leaks
        controller.detachView()
    }

    // ===== LoginController.LoginView Interface Methods =====

    override fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnLogin.isEnabled = false
        binding.etEmail.isEnabled = false
        binding.etPassword.isEnabled = false
    }

    override fun hideLoading() {
        binding.progressBar.visibility = View.GONE
        binding.btnLogin.isEnabled = true
        binding.etEmail.isEnabled = true
        binding.etPassword.isEnabled = true
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun showSuccess(userName: String) {
        Toast.makeText(
            this,
            "Login successful! Welcome $userName",
            Toast.LENGTH_LONG
        ).show()

        finish()
    }


    override fun showValidationError(field: String, message: String) {
        when (field) {
            "email" -> binding.tilEmail.error = message
            "password" -> binding.tilPassword.error = message
        }
    }
}