package com.starorigins.stockify.widgetapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import com.starorigins.stockify.widgetapp.home.MainActivity

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val animation = findViewById<LottieAnimationView>(R.id.splashAnimation)
        animation.playAnimation()


        val sharedPreferences = getSharedPreferences("user_logged", Context.MODE_PRIVATE)!!;


        Handler(Looper.getMainLooper()).postDelayed({
            val isLoggedIn = sharedPreferences.contains("is_logged_in")
            val intent = if (isLoggedIn) {
                Intent(this, MainActivity::class.java)
            } else {
                Intent(this, login::class.java)
            }
            startActivity(intent)
            finish()

            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN){
                param(FirebaseAnalytics.Param.METHOD, "appStart")
            }

        }, 3000)


    }
}