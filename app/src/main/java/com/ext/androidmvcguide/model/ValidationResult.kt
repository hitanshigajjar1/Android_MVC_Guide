package com.ext.androidmvcguide.model

sealed class ValidationResult {
    object Success : ValidationResult()
    object InvalidEmail : ValidationResult()
    object InvalidPassword : ValidationResult()
}