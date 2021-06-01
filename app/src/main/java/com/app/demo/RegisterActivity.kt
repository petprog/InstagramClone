package com.app.demo

import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.app.demo.databinding.ActivityRegisterBinding
import com.app.demo.utils.createShortToast
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        binding.register.setOnClickListener {
            val txtEmail = binding.email.text.toString()
            val txtPassword = binding.password.text.toString()

            if (TextUtils.isEmpty(txtEmail) || TextUtils.isEmpty(txtPassword)) {
                createShortToast("Enter your credentials")
            } else if (txtPassword.length < 6) {
                createShortToast("Password is too short")
            } else {
                registerUser(txtEmail, txtPassword)
            }
        }

    }

    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                createShortToast("Registration successful")
            } else {
                createShortToast("Registration failed")
            }

        }
    }
}