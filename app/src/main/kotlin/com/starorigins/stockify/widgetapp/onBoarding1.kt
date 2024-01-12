package com.starorigins.stockify.widgetapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class onBoarding1 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_boarding1)

        val onBoard1Btn= findViewById<Button>(R.id.oneBoard1Btn)

        onBoard1Btn.setOnClickListener {
            startActivity(Intent(this, onBoard2::class.java))
        }

    }
}