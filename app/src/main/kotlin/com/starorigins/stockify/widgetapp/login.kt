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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.starorigins.stockify.widgetapp.databinding.ActivityLoginBinding
import com.starorigins.stockify.widgetapp.home.MainActivity
import com.starorigins.stockify.widgetapp.model.User
import dagger.hilt.android.AndroidEntryPoint



@AndroidEntryPoint
class login : AppCompatActivity() {
    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


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
                var user = User(binding.email.text.toString(),
                    binding.password.text.toString())

                Firebase.auth.signInWithEmailAndPassword(user.email!! , user.password!!)
                    .addOnCompleteListener{
                        if(it.isSuccessful){
                            startActivity(Intent(this@login, MainActivity::class.java))
                            finish()
                        }else{
                            Toast.makeText(this@login, it.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
            }

        }
        binding.signUpTxt.setOnClickListener {
            startActivity(Intent(this@login, register::class.java))
            finish()
        }
    }

    fun isEmailValid(email: String): Boolean {
        val regexPattern = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
        return regexPattern.matches(email)
    }


}