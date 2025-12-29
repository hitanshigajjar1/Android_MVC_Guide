package com.ext.androidmvcguide.controller

import com.ext.androidmvcguide.model.LoginResult
import com.ext.androidmvcguide.model.User
import com.ext.androidmvcguide.model.UserRepository
import com.ext.androidmvcguide.model.ValidationResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * CONTROLLER: Handles user interactions and coordinates between Model and View
 * Contains the business logic flow
 */
class LoginController(
    private val repository: UserRepository = UserRepository()
) {

    interface LoginView {
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun showSuccess(userName: String)
        fun showValidationError(field: String, message: String)
    }

    private var view: LoginView? = null

    fun attachView(view: LoginView) {
        this.view = view
    }

    fun detachView() {
        this.view = null
    }

    fun login(email: String, password: String) {
        val user = User(email, password)

        // Validate input
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
                // Proceed with login
                performLogin(user)
            }
        }
    }

    private fun performLogin(user: User) {
        view?.showLoading()

        // Perform login on background thread
        CoroutineScope(Dispatchers.IO).launch {
            val result = repository.login(user.email, user.password)

            withContext(Dispatchers.Main) {
                view?.hideLoading()

                when (result) {
                    is LoginResult.Success -> {
                        view?.showSuccess(result.user.email)
                    }
                    is LoginResult.Failure -> {
                        view?.showError(result.message)
                    }
                }
            }
        }
    }
}