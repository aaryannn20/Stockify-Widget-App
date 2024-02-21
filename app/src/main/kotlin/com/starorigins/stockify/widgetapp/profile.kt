package com.starorigins.stockify.widgetapp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.auth.FirebaseAuth

class profile : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
            param(FirebaseAnalytics.Param.ITEM_ID, "ProfilePageActivity")
            param(FirebaseAnalytics.Param.ITEM_NAME, "Profile")
            param(FirebaseAnalytics.Param.CONTENT_TYPE, "ManageProfile")
        }


        auth= FirebaseAuth.getInstance()

        val tvName= findViewById<TextView>(R.id.tvName)
        val tvMail= findViewById<TextView>(R.id.tvMail)
        val backProfile= findViewById<ImageView>(R.id.backProfile)

        backProfile.setOnClickListener {
            onBackPressed()
        }

        tvName.text= auth.currentUser!!.displayName
        tvMail.text= auth.currentUser!!.email
    }
}