package com.example.smack.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smack.R
import com.example.smack.model.Message
import com.example.smack.services.UserDataService
import org.w3c.dom.Text

class MessageAdapter(val context: Context, val messages: ArrayList<Message>) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userImage = itemView.findViewById<ImageView>(R.id.messageUserAvatar)
        val userName = itemView.findViewById<TextView>(R.id.messageUserName)
        val messageBody = itemView.findViewById<TextView>(R.id.messageBody)
        val timeStamp = itemView.findViewById<TextView>(R.id.timestamp)

        fun bindMessage(context: Context, message: Message) {
            val resourceId = context.resources.getIdentifier(message.avatarName, "drawable", context.packageName)
            userImage.setImageResource(resourceId)
            userImage.setBackgroundColor(UserDataService.userAvatarColor(message.avatarColor))
            userName.text = message.userName
            messageBody.text = message.message
            timeStamp.text = message.timeStamp
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.message_list_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindMessage(context, messages[position])
    }

    override fun getItemCount(): Int {
        return messages.count()
    }
}