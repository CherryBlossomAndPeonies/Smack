package com.example.smack.controller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.getSystemService
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smack.databinding.ActivityMainBinding
import com.example.smack.R
import com.example.smack.adapters.MessageAdapter
import com.example.smack.model.Channel
import com.example.smack.model.Message
import com.example.smack.services.AuthService
import com.example.smack.services.MessageService
import com.example.smack.services.UserDataService
import com.example.smack.utilities.BROADCAST_USER_DATA_CHANGE
import com.example.smack.utilities.SOCKET_URL
import io.socket.client.IO
import io.socket.emitter.Emitter

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val socket = IO.socket(SOCKET_URL)
    private lateinit var channelAdapters: ArrayAdapter<Channel>
    private lateinit var messageAdapter: MessageAdapter
    private var selectedChannel: Channel? = null

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
        socket.on("messageCreated", messageCreated)
        setUpAdapter()

        if (App.sharedPrefs.isLoggedIn) {
            AuthService.findUserByEmail(this){}
        }

        findViewById<ListView>(R.id.channel_list_view).setOnItemClickListener { _, _, position, _ ->
            selectedChannel = MessageService.channels[position]
            updatechannelName()
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        findViewById<AppCompatImageButton>(R.id.sendMessageBtn).setOnClickListener {
            sendMessage()
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
            findViewById<ImageView>(R.id.navProfileId).setBackgroundColor(UserDataService.userAvatarColor(UserDataService.avatarColor))

            MessageService.getAllChannels(context){complete ->
                if (complete) {
                    if (MessageService.channels.count() > 0) {
                        channelAdapters.notifyDataSetChanged()
                        selectedChannel = MessageService.channels[0]
                        updatechannelName()
                    }
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
            MessageService.clearMessages()
            MessageService.clearChannels()
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

    private fun updatechannelName() {
        findViewById<TextView>(R.id.contentMessageHeader).text = selectedChannel.toString()
        MessageService.getAllMessages(selectedChannel!!.id) { complete ->
            if (complete) {
                messageAdapter.notifyDataSetChanged()
                if (messageAdapter.itemCount > 0) {
                    findViewById<RecyclerView>(R.id.messageListView).smoothScrollToPosition(messageAdapter.itemCount-1)
                }
            }
        }
    }

    private fun sendMessage() {
        if (App.sharedPrefs.isLoggedIn && findViewById<TextView>(R.id.messageInbox).text.isNotEmpty() && selectedChannel != null) {
            val userId = UserDataService.id
            val message = findViewById<TextView>(R.id.messageInbox).text.toString()
            val selectedChannelId = selectedChannel!!.id
            val avatarName = UserDataService.avatarImage
            val avatarColor = UserDataService.avatarColor
            val userName = UserDataService.name
            socket.emit("newMessage", message, userId, selectedChannelId, userName,avatarName, avatarColor)
            findViewById<MultiAutoCompleteTextView>(R.id.messageInbox).text.clear()
            hideKeyboard()
        }
    }

    private fun setUpAdapter() {
        channelAdapters = ArrayAdapter(this, android.R.layout.simple_list_item_1, MessageService.channels)
        findViewById<ListView>(R.id.channel_list_view).adapter = channelAdapters

        val layoutManager = LinearLayoutManager(this)
        messageAdapter = MessageAdapter(this, MessageService.messages)
        findViewById<RecyclerView>(R.id.messageListView).adapter = messageAdapter
        findViewById<RecyclerView>(R.id.messageListView).layoutManager = layoutManager
    }
    private val addChannel = Emitter.Listener { args ->
        if (App.sharedPrefs.isLoggedIn) {
            runOnUiThread {
                val channelName = args[0] as String
                val channelDescription = args[1] as String
                val channelId = args[2] as String

                var newchannel =  Channel(channelName, channelDescription, channelId)
                MessageService.channels.add(newchannel)
                channelAdapters.notifyDataSetChanged()
            }
        }
    }

    private val messageCreated = Emitter.Listener { args ->
        if (App.sharedPrefs.isLoggedIn) {
            runOnUiThread {
                val channelId = args[2] as String
                if (channelId == selectedChannel?.id) {
                    val message = args[0] as String
                    val userName = args[3] as String
                    val avatarName = args[4] as String
                    val avatarColor = args[5] as String
                    val messageId =  args[6] as String
                    val timeStamp = args[7] as String

                    val newMessage = Message(message, channelId, userName, avatarName, avatarColor, messageId, timeStamp)
                    MessageService.messages.add(newMessage)
                    messageAdapter.notifyDataSetChanged()
                    findViewById<RecyclerView>(R.id.messageListView).smoothScrollToPosition(messageAdapter.itemCount - 1)
                }
            }
        }
    }

    fun hideKeyboard() {
        var inputManager = getSystemService<InputMethodManager>()
        inputManager?.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

}