package com.ext.androidmvcguide.model

/**
 * MODEL: Represents the data structure for User
 * Contains business logic for validation
 */
data class User(
    val email: String,
    val password: String
) {

    fun isValidEmail(): Boolean {
        return email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(): Boolean {
        return password.isNotEmpty() && password.length >= 6
    }

    fun validate(): ValidationResult {
        return when {
            !isValidEmail() -> ValidationResult.InvalidEmail
            !isValidPassword() -> ValidationResult.InvalidPassword
            else -> ValidationResult.Success
        }
    }
}

