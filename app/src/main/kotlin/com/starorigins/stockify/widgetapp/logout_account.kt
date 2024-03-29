package com.starorigins.stockify.widgetapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class logout_account : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logout_account)

        val animation = findViewById<LottieAnimationView>(R.id.deleteAnimation)
        animation.playAnimation()


        auth= FirebaseAuth.getInstance()
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
            param(FirebaseAnalytics.Param.ITEM_ID, "logoutPageActivity")
            param(FirebaseAnalytics.Param.ITEM_NAME, "logout")
            param(FirebaseAnalytics.Param.CONTENT_TYPE, "AccountLogout")
        }



        val containerInfo= findViewById<ConstraintLayout>(R.id.deleteInfoContainer)
        val viewDelete= findViewById<ConstraintLayout>(R.id.viewDelete)
        val deletionLayout= findViewById<ConstraintLayout>(R.id.deletion)
        val deleteBtn= findViewById<Button>(R.id.deleteBtn)
        val logoutBtn= findViewById<ConstraintLayout>(R.id.viewLogout)
        val backBtn= findViewById<ImageView>(R.id.backDlt)


        backBtn.setOnClickListener {
            onBackPressed()
        }

        viewDelete.setOnClickListener {
            containerInfo.visibility= View.GONE
            deletionLayout.visibility= View.VISIBLE
        }

        checkLoginState()

        deleteBtn.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO){
                try {
                    auth.currentUser!!.delete().await()
                    withContext(Dispatchers.Main){
                        checkLoginState()
                        startActivity(Intent(this@logout_account, login::class.java))
                        finish()
                    }
                    val bundle = Bundle()
                    bundle.putString(FirebaseAnalytics.Param.METHOD, "AccountDeleted")
                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "accountDeletion")
                    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)
                } catch (e: Exception){
                    Toast.makeText(this@logout_account, e.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }

        logoutBtn.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO){
                auth.signOut()
                withContext(Dispatchers.Main){
                    if (checkLoginState()){
                        val bundle = Bundle()
                        bundle.putString(FirebaseAnalytics.Param.METHOD, "logout")
                        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "logout")
                        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)

                    }
                }
            }
        }
    }

    private fun checkLoginState(): Boolean {
        if (auth.currentUser==null){
            Toast.makeText(this, "Successfully applied actions!", Toast.LENGTH_SHORT).show()
            val intent= Intent(this, login::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            return true
        }
        return false
    }
}