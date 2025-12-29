package com.ext.androidmvcguide.model

/**
 * MODEL: Repository for handling user data operations
 * In real app, this would interact with database/API
 */
class UserRepository {

    // Simulated user database
    private val users = mutableListOf(
        User("admin@example.com", "admin123"),
        User("test@example.com", "test123")
    )

    fun login(email: String, password: String): LoginResult {
        // Simulate network delay
        Thread.sleep(1000)

        val user = users.find { it.email == email && it.password == password }
        return if (user != null) {
            LoginResult.Success(user)
        } else {
            LoginResult.Failure("Invalid email or password")
        }
    }

    fun register(user: User): RegisterResult {
        // Simulate network delay
        Thread.sleep(1000)

        val exists = users.any { it.email == user.email }
        return if (exists) {
            RegisterResult.UserExists
        } else {
            users.add(user)
            RegisterResult.Success
        }
    }
}

sealed class LoginResult {
    data class Success(val user: User) : LoginResult()
    data class Failure(val message: String) : LoginResult()
}

sealed class RegisterResult {
    object Success : RegisterResult()
    object UserExists : RegisterResult()
}