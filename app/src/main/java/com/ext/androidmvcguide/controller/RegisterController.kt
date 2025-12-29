package com.ext.androidmvcguide.controller

import com.ext.androidmvcguide.model.RegisterResult
import com.ext.androidmvcguide.model.User
import com.ext.androidmvcguide.model.UserRepository
import com.ext.androidmvcguide.model.ValidationResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * CONTROLLER: Handles user registration logic
 * Coordinates between RegisterView and Model
 */
class RegisterController(
    private val repository: UserRepository = UserRepository()
) {

    /**
     * Interface for View to implement
     * Defines all UI update methods
     */
    interface RegisterView {
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun showSuccess()
        fun showValidationError(field: String, message: String)
        fun navigateToLogin()
    }

    private var view: RegisterView? = null

    /**
     * Attach view to controller
     */
    fun attachView(view: RegisterView) {
        this.view = view
    }

    /**
     * Detach view to prevent memory leaks
     */
    fun detachView() {
        this.view = null
    }

    /**
     * Main registration method
     * @param email User's email
     * @param password User's password
     * @param confirmPassword Confirmation password
     */
    fun register(email: String, password: String, confirmPassword: String) {
        // First validate password match
        if (password != confirmPassword) {
            view?.showValidationError("confirmPassword", "Passwords do not match")
            return
        }

        val user = User(email, password)

        // Validate user input
        when (val validation = user.validate()) {
            is ValidationResult.InvalidEmail -> {
                view?.showValidationError("email", "Please enter a valid email address")
                return
            }
            is ValidationResult.InvalidPassword -> {
                view?.showValidationError("password", "Password must be at least 6 characters")
                return
            }
            is ValidationResult.Success -> {
                // Proceed with registration
                performRegistration(user)
            }
        }
    }

    /**
     * Performs async registration operation
     */
    private fun performRegistration(user: User) {
        view?.showLoading()

        // Perform registration on background thread
        CoroutineScope(Dispatchers.IO).launch {
            val result = repository.register(user)

            // Update UI on main thread
            withContext(Dispatchers.Main) {
                view?.hideLoading()

                when (result) {
                    is RegisterResult.Success -> {
                        view?.showSuccess()
                        // Navigate to login after short delay
                        kotlinx.coroutines.delay(1500)
                        view?.navigateToLogin()
                    }
                    is RegisterResult.UserExists -> {
                        view?.showError("User with this email already exists")
                    }
                }
            }
        }
    }
}