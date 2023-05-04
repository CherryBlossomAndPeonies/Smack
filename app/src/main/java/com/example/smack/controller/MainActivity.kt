package com.example.smack.controller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.getSystemService
import androidx.drawerlayout.widget.DrawerLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smack.databinding.ActivityMainBinding
import com.example.smack.R
import com.example.smack.model.Channel
import com.example.smack.services.AuthService
import com.example.smack.services.MessageService
import com.example.smack.services.UserDataService
import com.example.smack.utilities.BROADCAST_USER_DATA_CHANGE
import com.example.smack.utilities.SOCKET_URL
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import java.util.EventListener

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val socket = IO.socket(SOCKET_URL)
    private lateinit var channelAdapters: ArrayAdapter<Channel>

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
        //hideKeyboard()
        findViewById<AppCompatButton>(R.id.loginNavId).setOnClickListener { onLoginClick() }

        socket.connect()
        socket.on("channelCreated", addChannel)
        setUpAdapter()

        if (App.sharedPrefs.isLoggedIn) {
            AuthService.findUserByEmail(this){}
        }
    }

    override fun onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(onUserDataBroadCastReceiver, IntentFilter(
            BROADCAST_USER_DATA_CHANGE))
        super.onResume()
    }

    override fun onDestroy() {
        socket.disconnect()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onUserDataBroadCastReceiver)
        super.onDestroy()
    }

    private val onUserDataBroadCastReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            findViewById<TextView>(R.id.navUserNameId).text = UserDataService.name
            findViewById<TextView>(R.id.navEmailId).text = UserDataService.email
            findViewById<AppCompatButton>(R.id.loginNavId).text = "LOGOUT"
            var resourseId = resources.getIdentifier(UserDataService.avatarImage, "drawable", packageName)
            findViewById<ImageView>(R.id.navProfileId).setImageResource(resourseId)
            findViewById<ImageView>(R.id.navProfileId).setBackgroundColor(UserDataService.userAvatarColor())

            MessageService.getAllChannels(context){complete ->
                if (complete) {
                    channelAdapters.notifyDataSetChanged()
                    println("channels ${MessageService.channels.count()}")
                }
            }
        }
    }

    fun onLoginClick() {
        if (App.sharedPrefs.isLoggedIn) {
            UserDataService.logout()
            findViewById<ImageView>(R.id.navProfileId).setImageResource(R.drawable.profiledefault)
            findViewById<ImageView>(R.id.navProfileId).setBackgroundColor(resources.getColor(R.color.transparent, null))
            findViewById<AppCompatButton>(R.id.loginNavId).text = "LOGIN"
            findViewById<TextView>(R.id.navEmailId).text = ""
            findViewById<TextView>(R.id.navUserNameId).text = ""
            App.sharedPrefs.isLoggedIn = false
            MessageService.channels.clear()
        } else {
            var intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    fun onAddChannelClick(view: View) {
        if(App.sharedPrefs.isLoggedIn) {
            var builder = AlertDialog.Builder(this)
            var dialogView = layoutInflater.inflate(R.layout.add_channel_dialog, null)

            builder.setView(dialogView)
                .setPositiveButton("Add") {dialogInterface, i ->
                    // When the call back is called there is no dialog UI in the context
                    // hence we need to get that from the inflated UI
                    val nameTextField = dialogView.findViewById<TextView>(R.id.channelName)
                    val descriptionField = dialogView.findViewById<TextView>(R.id.channelDecription)
                    val name = nameTextField.text.toString()
                    val description = descriptionField.text.toString()
                    //hideKeyboard()

                    socket.emit("newChannel", name, description)
                }
                .setNegativeButton("Cancel") {dialogInterface, i ->
                    //hideKeyboard()
                }
                .show()
        }
    }

    private fun setUpAdapter() {
        channelAdapters = ArrayAdapter(this, android.R.layout.simple_list_item_1, MessageService.channels)
        findViewById<ListView>(R.id.channel_list_view).adapter = channelAdapters
    }
    private val addChannel = Emitter.Listener { args ->
        runOnUiThread {
            val channelName = args[0] as String
            val channelDescription = args[1] as String
            val channelId = args[2] as String

            var newchannel =  Channel(channelName, channelDescription, channelId)
            MessageService.channels.add(newchannel)
            channelAdapters.notifyDataSetChanged()
        }
    }

    fun hideKeyboard() {
        var inputManager = getSystemService<InputMethodManager>()
        inputManager?.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

}