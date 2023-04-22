package com.example.smack.controller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.drawerlayout.widget.DrawerLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.smack.databinding.ActivityMainBinding
import com.example.smack.R
import com.example.smack.services.AuthService
import com.example.smack.services.UserDataService
import com.example.smack.utilities.BROADCAST_USER_DATA_CHANGE

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
        LocalBroadcastManager.getInstance(this).registerReceiver(onUserDataBroadCastReceiver, IntentFilter(
            BROADCAST_USER_DATA_CHANGE))
    }

    private val onUserDataBroadCastReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            findViewById<TextView>(R.id.navUserNameId).text = UserDataService.name
            findViewById<TextView>(R.id.navEmailId).text = UserDataService.email
            findViewById<AppCompatButton>(R.id.loginNavId).text = "LOGOUT"
            var resourseId = resources.getIdentifier(UserDataService.avatarImage, "drawable", packageName)
            findViewById<ImageView>(R.id.navProfileId).setImageResource(resourseId)
            findViewById<ImageView>(R.id.navProfileId).setBackgroundColor(UserDataService.userAvatarColor())
        }
    }

    fun onLoginClick() {
        if (AuthService.isLoggedIn) {
            UserDataService.logout()
            findViewById<ImageView>(R.id.navProfileId).setImageResource(R.drawable.profiledefault)
            findViewById<ImageView>(R.id.navProfileId).setBackgroundColor(resources.getColor(R.color.transparent, null))
            findViewById<AppCompatButton>(R.id.loginNavId).text = "LOGIN"
            findViewById<TextView>(R.id.navEmailId).text = ""
            findViewById<TextView>(R.id.navUserNameId).text = ""
        } else {
            var intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    fun onAddChannelClick(view: View) {

    }
}