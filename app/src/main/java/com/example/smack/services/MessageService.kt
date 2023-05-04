package com.example.smack.services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.smack.model.Channel
import com.example.smack.utilities.GET_CHANNELS_URL
import org.json.JSONException

object MessageService {
    val channels = ArrayList<Channel>()

    fun getAllChannels(context: Context, complete:(Boolean) -> Unit) {
        val channelRequest = object : JsonArrayRequest(Method.GET, GET_CHANNELS_URL, null, Response.Listener { res ->
            try {
                for (i in 0 until res.length()) {
                    val jsonObject = res.getJSONObject(i)
                    val name = jsonObject.getString("name")
                    val description = jsonObject.getString("description")
                    val id = jsonObject.getString("_id")
                    val newChannel = Channel(name,description,id)
                    println("name ${name}")
                    channels.add(newChannel)
                }
                complete(true)
            } catch (ec: JSONException) {
                Log.d("Error", "Could not parse the response")
                complete(false)
            }
        }, Response.ErrorListener { err ->
            Log.d("ERROR", "Could not get channels")
            complete(false)
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset:utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization", "Bearer ${AuthService.authToken}")
                return headers
            }
        }
        Volley.newRequestQueue(context).add(channelRequest)
    }
}