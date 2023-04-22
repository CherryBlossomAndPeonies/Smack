package com.example.smack.services

import android.graphics.Color
import java.util.Scanner

object UserDataService {
    var id = ""
    var email = ""
    var avatarColor = ""
    var avatarImage = ""
    var name = ""

    fun logout() {
        id = ""
        email = ""
        avatarColor = ""
        avatarImage = ""
        name = ""
    }
    fun userAvatarColor(): Int {
        var r = 0;
        var g = 0;
        var b = 0;

        var stripedColor = avatarColor.replace(",", " ").replace("[", "").replace("]", "")
        var scanner = Scanner(stripedColor)

        println("color ${stripedColor}")
        if (scanner.hasNext()) {
            r = (scanner.nextDouble() * 255).toInt()
            g = (scanner.nextDouble() * 255).toInt()
            b = (scanner.nextDouble() * 255).toInt()
        }
        return Color.rgb(r, g, b)
    }
}