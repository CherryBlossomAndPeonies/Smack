package com.example.smack.model

import androidx.recyclerview.widget.RecyclerView.Adapter

class Channel (val name: String, val description: String, val id: String) {
    override fun toString(): String {
        return "#${name}"
    }
}