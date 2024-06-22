package com.starorigins.stockify.widgetapp

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.starorigins.stockify.widgetapp.AuthApi.userService
import com.starorigins.stockify.widgetapp.databinding.ActivityRegisterBinding
import com.starorigins.stockify.widgetapp.home.MainActivity
import com.starorigins.stockify.widgetapp.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class register : AppCompatActivity() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

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

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
            param(FirebaseAnalytics.Param.ITEM_ID, "RegisterPageActivity")
            param(FirebaseAnalytics.Param.ITEM_NAME, "username")
            param(FirebaseAnalytics.Param.CONTENT_TYPE, "credential")
        }



        binding.signUpBTN.setOnClickListener {
            val registerRequest = RegisterRequest()
            if(checkEmail() && checkPass() && checkLastFirstName()) {

                registerRequest.email = findViewById<EditText>(R.id.etMailTxt).text.toString()
                registerRequest.username =
                    findViewById<EditText>(R.id.editTextText).text.toString() + findViewById<EditText>(
                        R.id.etLastName
                    ).text.toString()
                registerRequest.password = findViewById<EditText>(R.id.etPass).text.toString()
            }
            registerUser(registerRequest)
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.METHOD, "signUpSuccess")
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle)
        }

        binding.signuptxt.setOnClickListener {
            startActivity(Intent(this@register, login::class.java))
            finish()
        }
    }

    private fun registerUser(registerRequest: RegisterRequest){
        val registerResponseCall = userService().registerFun(registerRequest)
        registerResponseCall!!.enqueue(object : Callback<RegisterResponse>{

            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful) {
                    val message = "Successful ..."
                    Toast.makeText(this@register,message,Toast.LENGTH_SHORT).show()
                    loggedIn()
                } else {
                    val message = "An error occurred please try again later ..."
                    Toast.makeText(this@register, message, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, throwable: Throwable) {
                val message = throwable.localizedMessage
                Toast.makeText(this@register,message,Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun loggedIn(){
            val intent= Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
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