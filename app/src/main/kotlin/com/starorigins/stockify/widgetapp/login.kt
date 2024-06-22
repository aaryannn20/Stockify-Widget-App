package com.starorigins.stockify.widgetapp


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.starorigins.stockify.widgetapp.databinding.ActivityLoginBinding
import com.starorigins.stockify.widgetapp.home.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@AndroidEntryPoint
class login : AppCompatActivity() {
    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
            param(FirebaseAnalytics.Param.ITEM_ID, "loginPageActivity")
            param(FirebaseAnalytics.Param.ITEM_NAME, "username")
            param(FirebaseAnalytics.Param.CONTENT_TYPE, "credential")
        }





        val text = "<font color=#1E88E5>Sign Up</font>"
        binding.signUpTxt.setText(Html.fromHtml(text))

        val animation = findViewById<LottieAnimationView>(R.id.loginAnimation)
        animation.playAnimation()


        val forgot_pass= findViewById<TextView>(R.id.forgot_email)

        forgot_pass.setOnClickListener {
            val viewPassReset: View = layoutInflater.inflate(R.layout.reset_password, null)
            val dialogReset= BottomSheetDialog(this)
            dialogReset.setContentView(viewPassReset)

            val send_mail= viewPassReset.findViewById<TextView>(R.id.resetEmailBtn)
            val reset_email= viewPassReset.findViewById<EditText>(R.id.resetEmailEt)

            send_mail.setOnClickListener {
                if (!reset_email.text.isNullOrBlank() || !reset_email.text.isNullOrEmpty()){
                    if (isEmailValid(reset_email.text.toString())){
                        Firebase.auth.sendPasswordResetEmail(reset_email.text.toString())
                            .addOnCompleteListener {
                                if (it.isSuccessful){
                                    Toast.makeText(this, "Email sent successfully! Check mail!", Toast.LENGTH_SHORT).show()
                                    dialogReset.dismiss()
                                } else {
                                    Toast.makeText(this, "Password reset mail failed to be sent! Try again!", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Toast.makeText(this, "Enter a valid email id!", Toast.LENGTH_SHORT).show()
                    }
                } else{
                    Toast.makeText(this, "Field cannot be empty!", Toast.LENGTH_SHORT).show()
                }
            }

            dialogReset.show()
        }

        binding.loginBTN.setOnClickListener {
            if(binding.email.text.toString().equals("") or
                binding.password.text.toString().equals("")){
                Toast.makeText(this@login, "Please fill the details", Toast.LENGTH_SHORT).show()
            }else{
                val loginRequest = LoginRequest()
                loginRequest.email = binding.email.text.toString()
                loginRequest.password = binding.password.text.toString()
                loginUser(loginRequest)
                val bundle = Bundle()
                bundle.putString(FirebaseAnalytics.Param.METHOD, "loginSuccess")
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
            }

        }
        binding.signUpTxt.setOnClickListener {
            startActivity(Intent(this@login, register::class.java))
            finish()

            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM){
                param(FirebaseAnalytics.Param.METHOD,"signUpScreen")
            }

        }
    }

    fun isEmailValid(email: String): Boolean {
        val regexPattern = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
        return regexPattern.matches(email)
    }


    private fun loginUser(loginRequest: LoginRequest){
        val loginRequestCall = AuthApi.userService().loginFun(loginRequest)
        loginRequestCall!!.enqueue(object : Callback<LoginResponse> {

            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    val intent = Intent(this@login, MainActivity::class.java)
                    intent.putExtra("data", loginResponse)
                    startActivity(intent)

                } else {
                    val message = "An error occurred please try again later ..."
                    Toast.makeText(this@login, message, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, throwable: Throwable) {
                val message = throwable.localizedMessage
                Toast.makeText(this@login,message,Toast.LENGTH_SHORT).show()
            }
        })
    }


}