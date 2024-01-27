package com.starorigins.stockify.widgetapp

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.starorigins.stockify.widgetapp.databinding.ActivityRegisterBinding
import com.starorigins.stockify.widgetapp.home.MainActivity
import com.starorigins.stockify.widgetapp.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class register : AppCompatActivity() {

    val binding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }
    lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val text = "<font color=#1E88E5>Login</font>"
        binding.signuptxt.setText(Html.fromHtml(text))

        val animation = findViewById<LottieAnimationView>(R.id.lottieAnimationView)
        animation.playAnimation()

        user = User()

        binding.signUpBTN.setOnClickListener {
            Toast.makeText(this, "Registering User...", Toast.LENGTH_SHORT).show()
            registerUser()
        }

        binding.signuptxt.setOnClickListener {
            startActivity(Intent(this@register, login::class.java))
            finish()
        }
    }

    private fun registerUser(){
        val email= findViewById<EditText>(R.id.etMailTxt).text.toString()
        val password= findViewById<EditText>(R.id.etPass).text.toString()
        val firstName= findViewById<EditText>(R.id.editTextText).text.toString()
        val lastName= findViewById<EditText>(R.id.etLastName).text.toString()

        if (checkEmail() && checkPass() && checkLastFirstName()){
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    Firebase.auth.createUserWithEmailAndPassword(email, password).await()
                    Firebase.auth.currentUser?.let {
                        val userprofile= UserProfileChangeRequest.Builder()
                            .setDisplayName("$firstName $lastName")
                            .build()
                        it.updateProfile(userprofile).await()
                    }
                    withContext(Dispatchers.Main){
                        checkLoggedInStatus()
                    }
                } catch (e: Exception){
                    withContext(Dispatchers.Main){
                        e.message?.let { Toast.makeText(this@register, it, Toast.LENGTH_SHORT).show() }
                    }
                }
            }
        }
    }

    private fun checkLoggedInStatus(){
        if (Firebase.auth.currentUser==null){
            Toast.makeText(this@register, "Something went wrong, try again!", Toast.LENGTH_SHORT).show()
        } else {
            val intent= Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun checkEmail(): Boolean {
        val email= findViewById<EditText>(R.id.etMailTxt).text.toString()
        if (email.isEmpty()) {
            Toast.makeText(this@register, "Email is needed to proceed forward", Toast.LENGTH_SHORT).show()
            return false
        } else if (!Regex("^\\w+[.-]?\\w+@\\w+([.-]?\\w+)+\$").matches(email)) {
            Toast.makeText(this@register, "Email should be in the specified format", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun checkPass(): Boolean {
        val password= findViewById<EditText>(R.id.etPass).text.toString()
        if (password.isEmpty()){
            Toast.makeText(this@register, "Password can't be empty", Toast.LENGTH_SHORT).show()
            return false
        } else if (password.isNotEmpty() && password.length<8){
            Toast.makeText(this@register, "Password should be minimum 8 characters long", Toast.LENGTH_SHORT).show()
        }
        return true
    }

    private fun checkLastFirstName(): Boolean{
        val firstName= findViewById<EditText>(R.id.editTextText).text.toString()
        val lastName= findViewById<EditText>(R.id.etLastName).text.toString()

        if (firstName.isEmpty()){
            Toast.makeText(this@register, "First name cannot be empty", Toast.LENGTH_SHORT).show()
            return false
        } else if (lastName.isEmpty()){
            Toast.makeText(this@register, "Last name cannot be empty", Toast.LENGTH_SHORT).show()
            return false
        } else if (!Regex("[a-zA-Z]+").matches(firstName)){
            Toast.makeText(this@register, "First name can only contain alphabets", Toast.LENGTH_SHORT).show()
            return false
        } else if (!Regex("[a-zA-Z]+").matches(lastName)){
            Toast.makeText(this@register, "Last name can only contain alphabets", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }


    companion object {
        const val USER_NODES = "User"
    }
}