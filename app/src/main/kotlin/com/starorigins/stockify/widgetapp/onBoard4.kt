package com.starorigins.stockify.widgetapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button


class onBoard4 : AppCompatActivity() {


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_board4)

        val onBoard4Btn= findViewById<Button>(R.id.oneBoard4Btn)


        onBoard4Btn.setOnClickListener {
            val sharedPreferences: SharedPreferences = getSharedPreferences("loginKey", Context.MODE_PRIVATE)
            val editor= sharedPreferences.edit()
            editor.putBoolean("viewedOnBoarding", true).apply()

            val intent= Intent(this, login::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}