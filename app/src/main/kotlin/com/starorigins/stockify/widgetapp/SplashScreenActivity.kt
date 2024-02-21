package com.starorigins.stockify.widgetapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.auth.FirebaseAuth
import com.starorigins.stockify.widgetapp.home.MainActivity

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val animation = findViewById<LottieAnimationView>(R.id.splashAnimation)
        animation.playAnimation()


        Handler(Looper.getMainLooper()).postDelayed({
            if (FirebaseAuth.getInstance().currentUser == null)
                startActivity(Intent(this, login::class.java))
            else
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN){
                param(FirebaseAnalytics.Param.METHOD, "appStart")
            }

        }, 3000)
    }
}