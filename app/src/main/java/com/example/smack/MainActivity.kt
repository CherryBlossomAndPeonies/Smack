package com.example.smack

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.ui.AppBarConfiguration
import com.example.smack.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.example.smack.R

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, binding.appBarMain.toolbar, R.string.open_nav , R.string.close_nav
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        findViewById<AppCompatButton>(R.id.loginNavId).setOnClickListener { onLoginClick() }
    }

    fun onLoginClick() {
        var intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    fun onAddChannelClick(view: View) {

    }
}