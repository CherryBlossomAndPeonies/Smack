package com.example.smack.services

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.smack.utilities.REGISTER_URL
import org.json.JSONObject

object AuthService {
    fun registerUser(context:Context, email: String, password: String, complete:(Boolean) -> Unit) {
        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)
        val requestBody = jsonBody.toString()

        val registerRequest = object : StringRequest(Request.Method.POST, REGISTER_URL, Response.Listener {res->
            println(res)
            complete(true)
        }, Response.ErrorListener {t ->
            println("t" + t.message)
            Log.d("ERROR", "Could not register user")
            complete(false)
        }) {
            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }
        }

        Volley.newRequestQueue(context).add(registerRequest)
    }
}