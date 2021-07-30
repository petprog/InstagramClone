package com.app.demo

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.app.demo.databinding.ActivityRegisterBinding
import com.app.demo.utils.createProgressBar
import com.app.demo.utils.createShortToast
import com.app.demo.utils.openActivity
import com.app.demo.utils.openActivityWithFlags
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private lateinit var mRootRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        mRootRef = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()

        binding.loginUser.setOnClickListener {
            openActivity(LoginActivity::class.java)
        }

        binding.register.setOnClickListener {
            val txtUsername = binding.username.text.toString()
            val txtName = binding.name.text.toString()
            val txtEmail = binding.email.text.toString()
            val txtPassword = binding.password.text.toString()

            if (TextUtils.isEmpty(txtUsername) || TextUtils.isEmpty(txtName)
                || TextUtils.isEmpty(txtEmail) || TextUtils.isEmpty(txtPassword)
            ) {
                createShortToast("Empty credentials!")
            } else if (txtPassword.length < 6) {
                createShortToast("Password too short!")
            } else {
                registerUser(txtUsername, txtName, txtEmail, txtPassword)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun registerUser(
        username: String,
        name: String,
        email: String,
        password: String,
    ) {
        val progressBar = createProgressBar(this, binding.ll)
        progressBar.visibility = View.VISIBLE

        mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
            val map = HashMap<String, Any>()
            map["name"] = name
            map["email"] = email
            map["username"] = username
            map["id"] = mAuth.currentUser?.uid!!
            map["bio"] = ""
            map["imageurl"] = "default"


            mRootRef.child("Users").child(mAuth.currentUser?.uid!!).setValue(map)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        progressBar.visibility = View.GONE
                        createShortToast("Update profile for better experience")
                        openActivityWithFlags(MainActivity::class.java)
                        finish()
                    }

                }
        }.addOnFailureListener { e ->
            progressBar.visibility = View.GONE
            createShortToast(e.message!!)
        }
    }
}