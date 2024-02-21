package com.starorigins.stockify.widgetapp.settings

import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.starorigins.stockify.widgetapp.AppPreferences
import com.starorigins.stockify.widgetapp.R
import com.starorigins.stockify.widgetapp.hasNotificationPermission
import com.starorigins.stockify.widgetapp.logout_account
import com.starorigins.stockify.widgetapp.profile
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SettingsFragment : Fragment() {
    lateinit var auth: FirebaseAuth
    private lateinit var appPreferences: AppPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settingschild, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userName= view.findViewById<TextView>(R.id.tvNameMore)
        val viewProfile= view.findViewById<View>(R.id.viewProfile)
        val viewEmail= view.findViewById<ConstraintLayout>(R.id.viewProfileEmail)
        val emailImgBtn= view.findViewById<ImageView>(R.id.emailVerifyBtn)
        val emailImgTv= view.findViewById<TextView>(R.id.emailVerifyTv)
        val changeMail= view.findViewById<TextView>(R.id.changeMail)
        val accountDelete= view.findViewById<ConstraintLayout>(R.id.accountDelete)
        val contact = view.findViewById<ConstraintLayout>(R.id.viewHelp)
        val notificationCheckbox = view.findViewById<CheckBox>(R.id.notificationCheckBox)

        contact.setOnClickListener {
            startActivity(Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto","aryanmishra15243@gmail.com",null)))
        }

        if (Build.VERSION.SDK_INT >= 33 && context?.hasNotificationPermission() == true){
            notificationCheckbox.isChecked = true
        }
        notificationCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                // Show toast message when checkbox is not checked
                Toast.makeText(context, "Update the Notification Setting to receive latest alerts and notification", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        accountDelete.setOnClickListener {
            startActivity(Intent(requireContext(), logout_account::class.java))
        }

        viewProfile.setOnClickListener {
            startActivity(Intent(requireContext(), profile::class.java))
        }
        auth= FirebaseAuth.getInstance()

        userName.text= auth.currentUser?.displayName

        if (auth.currentUser!!.isEmailVerified){
            emailImgBtn.visibility= View.GONE
            changeMail.visibility= View.VISIBLE
            emailImgTv.text= "Email id verified"
            emailImgTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.deactiveText))
            view.findViewById<ConstraintLayout>(R.id.viewProfileEmail).backgroundTintList= ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), R.color.viewDashLowOpac)
            )
        } else {
            viewEmail.setOnClickListener {
                auth.currentUser!!.sendEmailVerification()
                Toast.makeText(requireContext(), "Verification mail send to registered mail id!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}