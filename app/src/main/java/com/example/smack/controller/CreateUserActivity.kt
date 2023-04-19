package com.example.smack.controller

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
            AuthService.registerUser(this, binding.createUserNameText.text.toString(), binding.createUserPasswordText.text.toString()) { complete ->
                if (complete) {
                    AuthService.loginUser(this,binding.createUserNameText.text.toString(), binding.createUserPasswordText.text.toString()) { loginComplete ->
                        if(loginComplete) {
                            println("User name: "+ AuthService.userName)
                            println("Password: "+ AuthService.authToken)
                        }
                    }
                }
            }
        }
    }
}