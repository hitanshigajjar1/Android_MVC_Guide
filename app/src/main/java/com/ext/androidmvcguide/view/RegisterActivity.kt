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
import com.ext.androidmvcguide.controller.RegisterController
import com.ext.androidmvcguide.databinding.ActivityRegisterBinding

/**
 * VIEW: Registration Screen
 * Implements RegisterController.RegisterView interface
 * Handles UI updates and user input for registration
 */
class RegisterActivity : AppCompatActivity(), RegisterController.RegisterView {

    private lateinit var binding: ActivityRegisterBinding
    private val controller = RegisterController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRegisterBinding.inflate(layoutInflater)
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
        // Register button click
        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            // Clear previous errors
            binding.tilEmail.error = null
            binding.tilPassword.error = null
            binding.tilConfirmPassword.error = null

            // Delegate to controller
            controller.register(email, password, confirmPassword)
        }

        // Navigate to login screen
        binding.tvLoginHere.setOnClickListener {
            finish() // Go back to previous screen
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Detach controller to prevent memory leaks
        controller.detachView()
    }

    // ===== RegisterController.RegisterView Interface Methods =====

    override fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnRegister.isEnabled = false
        binding.etEmail.isEnabled = false
        binding.etPassword.isEnabled = false
        binding.etConfirmPassword.isEnabled = false
    }

    override fun hideLoading() {
        binding.progressBar.visibility = View.GONE
        binding.btnRegister.isEnabled = true
        binding.etEmail.isEnabled = true
        binding.etPassword.isEnabled = true
        binding.etConfirmPassword.isEnabled = true
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun showSuccess() {
        Toast.makeText(
            this,
            getString(R.string.success_register),
            Toast.LENGTH_LONG
        ).show()
    }

    override fun showValidationError(field: String, message: String) {
        when (field) {
            "email" -> binding.tilEmail.error = message
            "password" -> binding.tilPassword.error = message
            "confirmPassword" -> binding.tilConfirmPassword.error = message
        }
    }

    override fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}