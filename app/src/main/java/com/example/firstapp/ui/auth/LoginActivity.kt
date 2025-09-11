// com/example/firstapp/ui/auth/LoginActivity.kt

package com.example.firstapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.firstapp.MainActivity
import com.example.firstapp.R
import com.example.firstapp.api.client.NetworkResult
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import android.widget.ProgressBar
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: LoginViewModel

    // UI elements
    private lateinit var editTextUsername: TextInputEditText
    private lateinit var editTextPassword: TextInputEditText
    private lateinit var buttonLogin: MaterialButton
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("LoginActivity", "LoginActivity started")

        setContentView(R.layout.activity_login)

        // Initialize UI elements
        editTextUsername = findViewById(R.id.editText_username)
        editTextPassword = findViewById(R.id.editText_password)
        buttonLogin = findViewById(R.id.button_login)
        progressBar = findViewById(R.id.progressBar)

        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        // Перевірити чи вже авторизований (якщо так - перенаправити)
        if (viewModel.isLoggedIn()) {
            Log.d("LoginActivity", "User is already logged in, redirecting to MainActivity")
            navigateToMain()
            return
        }

        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        viewModel.loginResult.observe(this) { result ->
            Log.d("LoginActivity", "Login result: ${result::class.simpleName}")

            when (result) {
                is NetworkResult.Loading -> {
                    Log.d("LoginActivity", "Login in progress...")
                    showLoading(true)
                }
                is NetworkResult.Success -> {
                    Log.d("LoginActivity", "Login successful!")
                    showLoading(false)
                    Toast.makeText(this, "Успішна авторизація!", Toast.LENGTH_SHORT).show()
                    navigateToMain()
                }
                is NetworkResult.Error -> {
                    Log.e("LoginActivity", "Login error: ${result.message}")
                    showLoading(false)
                    Toast.makeText(this, result.message ?: "Помилка авторизації", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setupClickListeners() {
        buttonLogin.setOnClickListener {
            val username = editTextUsername.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            Log.d("LoginActivity", "Login button clicked with username: $username")

            if (validateInput(username, password)) {
                Log.d("LoginActivity", "Input validated, starting login...")
                viewModel.login(username, password)
            }
        }
    }

    private fun validateInput(username: String, password: String): Boolean {
        if (username.isEmpty()) {
            editTextUsername.error = "Введіть ім'я користувача"
            Log.w("LoginActivity", "Username is empty")
            return false
        }

        if (password.isEmpty()) {
            editTextPassword.error = "Введіть пароль"
            Log.w("LoginActivity", "Password is empty")
            return false
        }

        Log.d("LoginActivity", "Input validation passed")
        return true
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        buttonLogin.isEnabled = !isLoading
        editTextUsername.isEnabled = !isLoading
        editTextPassword.isEnabled = !isLoading
    }

    private fun navigateToMain() {
        Log.d("LoginActivity", "Navigating to MainActivity")
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
