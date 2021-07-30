package com.app.demo

import android.R
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.app.demo.databinding.ActivityLoginBinding
import com.app.demo.utils.createShortToast
import com.app.demo.utils.openActivityWithFlags
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()

        binding.registerUser.setOnClickListener {
            openActivityWithFlags(RegisterActivity::class.java)
        }

        binding.login.setOnClickListener {
            val txtEmail = binding.email.text.toString()
            val txtPassword = binding.password.text.toString()

            if (TextUtils.isEmpty(txtEmail) || TextUtils.isEmpty(txtPassword)) {
                createShortToast("Empty credentials!")
            } else if (txtPassword.length < 6) {
                createShortToast("Password too short!")
            } else {
                loginUser(txtEmail, txtPassword)
            }
        }

    }

    private fun loginUser(email: String, password: String) {
        val progressBar = ProgressBar(this, null, R.attr.progressBarStyleSmallTitle)
        val params = RelativeLayout.LayoutParams(100, 100)
        params.addRule(RelativeLayout.CENTER_IN_PARENT)
        binding.ll.addView(progressBar, params)
        progressBar.visibility = View.VISIBLE

        mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            progressBar.visibility = View.GONE
            createShortToast("Login Successful!")
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }.addOnFailureListener {
            progressBar.visibility = View.GONE
            createShortToast("Login Failed!")
        }
    }
}