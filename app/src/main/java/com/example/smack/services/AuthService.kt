package com.example.smack.services

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.smack.utilities.ADD_USER_URL
import com.example.smack.utilities.LOGIN_URL
import com.example.smack.utilities.REGISTER_URL
import org.json.JSONException
import org.json.JSONObject
import java.util.Objects
import kotlin.contracts.Returns

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

    fun addUser(context: Context, email: String, name: String, avatarColor: String, avatarName: String, authToken:String, complete: (Boolean) -> Unit) {
        var jsonBody = JSONObject()
        jsonBody.put("name", name)
        jsonBody.put("email", email)
        jsonBody.put("avatarName", avatarName)
        jsonBody.put("avatarColor", avatarColor)
        val requestBody = jsonBody.toString()

        val addUserRequest = object : JsonObjectRequest(Method.POST, ADD_USER_URL, null, Response.Listener { response ->
            try {
                UserDataService.id = response.getString("_id")
                UserDataService.email = response.getString("email")
                UserDataService.avatarColor = response.getString("avatarColor")
                UserDataService.avatarImage = response.getString("avatarName")
                UserDataService.name = response.getString("name")
                complete(true)
            } catch (e: JSONException) {
                complete(false)
                Log.d("ERROR", "EXC:"+e.message)
            }
        }, Response.ErrorListener { error ->
                Log.d("ERROR", "Could not add user ${error}")
                complete(false)
        }) {
            override fun
                    getBody(): ByteArray {
                return requestBody.toByteArray()
            }

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization", "Bearer ${authToken}")
                return headers;
            }
        }

        Volley.newRequestQueue(context).add(addUserRequest)
    }
}