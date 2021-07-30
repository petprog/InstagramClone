package com.app.demo

import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.appcompat.app.AppCompatActivity
import com.app.demo.databinding.ActivityStartBinding
import com.app.demo.utils.openActivity
import com.app.demo.utils.openActivityWithFlags
import com.google.firebase.auth.FirebaseAuth

class StartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.linearLayout.animate().alpha(0f).duration = 10
        val animation = TranslateAnimation(0f, 0f, 0f, -1000f)
        animation.duration = 1000L
        animation.fillAfter = false
        animation.setAnimationListener(MyAnimationListener())
        binding.iconImage.animation = animation

        binding.register.setOnClickListener {
            openActivityWithFlags(RegisterActivity::class.java)
        }

        binding.login.setOnClickListener {
            openActivityWithFlags(LoginActivity::class.java)
        }
    }

    private inner class MyAnimationListener : Animation.AnimationListener {
        override fun onAnimationStart(p0: Animation?) = Unit
        override fun onAnimationEnd(p0: Animation?) {
            binding.iconImage.clearAnimation()
            binding.iconImage.visibility = View.INVISIBLE
            binding.linearLayout.animate().alpha(1f).duration = 1000
        }

        override fun onAnimationRepeat(p0: Animation?) = Unit

    }

    override fun onStart() {
        super.onStart()
        if (FirebaseAuth.getInstance().currentUser != null) {
            openActivity(MainActivity::class.java)
            finish()
        }
    }
}