package com.example.smack.services

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.smack.utilities.LOGIN_URL
import com.example.smack.utilities.REGISTER_URL
import org.json.JSONException
import org.json.JSONObject
import java.util.Objects

object AuthService {

    var userName = ""
    var authToken = ""
    var isLoggedIn = false
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

    fun loginUser(context:Context, email: String, password: String, complete: (Boolean) -> Unit) {
        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)
        val requestBody = jsonBody.toString()

        val loginRequest = object : JsonObjectRequest(Method.POST, LOGIN_URL, null, Response.Listener { response ->
           try {
               userName = response.getString("user")
               authToken = response.getString("token")
               isLoggedIn = true
               complete(true)
           } catch (e: JSONException){
                Log.d("JSON", "Exc:"+e.message)
                complete(false)
           }
        }, Response.ErrorListener { err ->
            Log.d("Error", "Could not Login User ${err}")
            complete(false)
        }) {
            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }
        }

        Volley.newRequestQueue(context).add(loginRequest)
    }
}