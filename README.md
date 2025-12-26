# Android MVC Architecture Guide 🏗️

A concise guide for implementing **Model-View-Controller (MVC)** architecture in Android.

---

## 🎯 What is MVC?

MVC separates your application into three main components:

```
VIEW (Activity/Fragment)
    ↓ user actions
CONTROLLER (Coordinator)
    ↓ requests data
MODEL (Repository + Business Logic)
```

**Benefits:**
- ✅ Clear Separation of Concerns
- ✅ Easier Testing
- ✅ Simple to Understand
- ✅ No Framework Dependencies

---

## 📁 Project Structure

```
app/src/main/java/com/example/app/
│
├── model/                          # MODEL LAYER
│   ├── User.kt
│   ├── UserRepository.kt
│   └── ValidationResult.kt
│
├── controller/                     # CONTROLLER LAYER
│   ├── LoginController.kt
│   └── RegisterController.kt
│
└── view/                           # VIEW LAYER
    ├── LoginActivity.kt
    └── RegisterActivity.kt
```

---

## 🚀 Implementation

### 1. Setup Dependencies

```kotlin
dependencies {
    // Core Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.3.1")
}
```

### 2. Enable ViewBinding

```kotlin
android {
    buildFeatures {
        viewBinding = true
    }
}
```

### 3. Model Layer

```kotlin
// model/User.kt
data class User(
    val email: String,
    val password: String
) {
    fun isValidEmail(): Boolean
    fun isValidPassword(): Boolean
    fun validate(): ValidationResult
}

// model/UserRepository.kt
class UserRepository {
    suspend fun login(email: String, password: String): LoginResult
    suspend fun register(user: User): RegisterResult
}
```

### 4. Controller Layer

```kotlin
// controller/LoginController.kt
class LoginController(private val repository: UserRepository) {
    
    interface LoginView {
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun showSuccess(userName: String)
        fun showValidationError(field: String, message: String)
    }
    
    private var view: LoginView? = null
    
    fun attachView(view: LoginView)
    fun detachView()
    fun login(email: String, password: String)
}
```

### 5. View Layer

```kotlin
// view/LoginActivity.kt
class LoginActivity : AppCompatActivity(), LoginController.LoginView {
    
    private lateinit var binding: ActivityLoginBinding
    private val controller = LoginController(UserRepository())
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        controller.attachView(this)
        setupListeners()
    }
    
    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            controller.login(email, password)
        }
    }
    
    override fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }
    
    override fun hideLoading() {
        binding.progressBar.visibility = View.GONE
    }
    
    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    
    override fun showSuccess(userName: String) {
        Toast.makeText(this, "Welcome $userName!", Toast.LENGTH_SHORT).show()
    }
    
    override fun showValidationError(field: String, message: String) {
        when (field) {
            "email" -> binding.etEmail.error = message
            "password" -> binding.etPassword.error = message
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        controller.detachView()
    }
}
```

---

## 🔄 Data Flow

```
User Action → View → Controller → Model
                ↓                    ↓
            Observes              Validates
                ↓                    ↓
           UI Updates ←  Result ← Repository
```

**Example:**
1. User clicks "Login" → `controller.login(email, password)`
2. Controller validates → `user.validate()`
3. Controller fetches → `repository.login()`
4. Repository returns → `LoginResult.Success`
5. Controller updates → `view.showSuccess()`
6. View displays → Success message

---

## 🧪 Testing

```kotlin
class LoginControllerTest {
    
    private lateinit var controller: LoginController
    private lateinit var mockView: LoginController.LoginView
    
    @Test
    fun `login with invalid email shows validation error`() {
        controller.login("invalid-email", "password123")
        verify(mockView).showValidationError(eq("email"), any())
    }
}
```

---

## 📚 Best Practices

### ✅ DO's

- Keep View dumb (only UI logic)
- Use interfaces for communication
- Detach controller in `onDestroy()`
- Validate data in Model
- Use coroutines for async operations

### ❌ DON'Ts

- Don't access Model directly from View
- Don't put UI logic in Controller
- Don't forget to detach views
- Don't use static references


---

## 📖 Resources

- [Android Developer Guide](https://developer.android.com/)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [MVC Pattern](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller)

---
