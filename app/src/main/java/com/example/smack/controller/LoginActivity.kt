package com.example.smack.controller

import android.content.Intent
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.smack.databinding.ActivityLoginBinding
import com.example.smack.services.AuthService

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginProgress.visibility = View.INVISIBLE

        binding.signUpBtn.setOnClickListener {
            var intent = Intent(this, CreateUserActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.loginBtn.setOnClickListener {
            if (binding.emailSigninText.text.isNotEmpty() && binding.passwordSigninText.text.isNotEmpty()) {
                showProgress(true)
                hideKeyBoard()
                AuthService.loginUser(this, binding.emailSigninText.text.toString(), binding.passwordSigninText.text.toString()){complete ->
                    if (complete) {
                        AuthService.findUserByEmail(this) { get_complete ->
                            if (get_complete) {
                                finish()
                            } else {
                                toastMessage()
                            }
                        }
                    } else {
                        toastMessage()
                    }
                }
            } else {
                Toast.makeText(this, "Email and password must contain value", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun toastMessage() {
        Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show()
        showProgress(false)
    }

    private fun showProgress(enable: Boolean) {
        if (enable) {
            binding.loginProgress.visibility = View.VISIBLE
        } else {
            binding.loginProgress.visibility = View.INVISIBLE
        }

        binding.loginBtn.isEnabled = !enable
        binding.signUpBtn.isEnabled = !enable
    }

    private fun hideKeyBoard() {
        val inputManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }
}