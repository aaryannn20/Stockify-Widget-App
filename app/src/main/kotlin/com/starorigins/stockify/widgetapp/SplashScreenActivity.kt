package com.starorigins.stockify.widgetapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val animation = findViewById<LottieAnimationView>(R.id.splashAnimation)
        animation.playAnimation()


//        Handler(Looper.getMainLooper()).postDelayed({
//
//            val intent = intent
//
//            if (intent.extras != null) {
//                loginResponse = (intent.getSerializableExtra("data") as LoginResponse?)!!
//                email = (loginResponse.email.toString())
//                Log.e("TAG", "msg >" + loginResponse.email)
//            }
//            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN){
//                param(FirebaseAnalytics.Param.METHOD, "appStart")
//            }
//
//        }, 3000)


    }
}