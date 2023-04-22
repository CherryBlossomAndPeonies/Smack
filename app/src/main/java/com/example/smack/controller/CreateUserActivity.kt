package com.example.smack.controller

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.smack.databinding.ActivityCreateUserBinding
import com.example.smack.services.AuthService
import kotlin.random.Random

class CreateUserActivity : AppCompatActivity() {
    lateinit var binding: ActivityCreateUserBinding
    var usedImage = "profiledefault"
    var usedColor = "[0.5, 0.5, 0.5, 1]"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.createUserProgress.visibility = View.INVISIBLE

        binding.createUserAvatarImage.setOnClickListener {
            var color = Random.nextInt(2)
            var avatar = Random.nextInt(28)

            usedImage = if (color == 0) {
                "light${avatar}"
            } else {
                "dark${avatar}"
            }

            println(avatar)
            var resourceId = resources.getIdentifier(usedImage, "drawable", packageName)

            binding.createUserAvatarImage.setImageResource(resourceId)
        }

        binding.generateBackgroundBtn.setOnClickListener {
            var r = Random.nextInt(255)
            var g = Random.nextInt(255)
            var b = Random.nextInt(255)

            binding.createUserAvatarImage.setBackgroundColor(Color.rgb(r, g, b))

            usedColor = "[${r.toDouble()/255},${g.toDouble()/255}, ${b.toDouble()/255},1]"
            println(r)
        }

        binding.createUserBtn.setOnClickListener {
            enableSpinner(true)
            AuthService.registerUser(this, binding.createUserEmailText.text.toString(), binding.createUserPasswordText.text.toString()) { complete ->
                if (complete) {
                    AuthService.loginUser(this,binding.createUserEmailText.text.toString(), binding.createUserPasswordText.text.toString()) { loginComplete ->
                        if(loginComplete) {
                            println("User name: "+ AuthService.userName)
                            println("user taken: "+ AuthService.authToken)
                            AuthService.addUser(this, binding.createUserEmailText.text.toString(), binding.createUserNameText.text.toString(), usedColor, usedImage, AuthService.authToken) { addUserComplete ->
                                if (addUserComplete) {
                                    println("Added user")
                                    finish()
                                } else {
                                    errorToast()
                                }
                            }
                        } else {
                            errorToast()
                        }
                    }
                } else {
                    errorToast()
                }
            }
        }
    }

    private fun errorToast() {
        Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show()
        enableSpinner(false)
    }

    private fun enableSpinner(enable:Boolean) {
        if(enable) {
            binding.createUserProgress.visibility = View.VISIBLE
        } else {
            binding.createUserProgress.visibility = View.INVISIBLE
        }

        binding.createUserBtn.isEnabled = !enable
        binding.generateBackgroundBtn.isEnabled = !enable
        binding.createUserAvatarImage.isEnabled = !enable
    }
}