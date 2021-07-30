/**
 * Created by Taiwo Farinu on 29-May-21
 */

package com.app.demo.utils

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast

fun Context.createShortToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun <T> Context.openActivity(c: Class<T>) {
    this.startActivity(Intent(this, c))
}

fun <T> Context.openActivityWithFlags(c: Class<T>) {
    this.startActivity(Intent(this,
        c).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP));
}

fun createProgressBar(context: Context, container: ViewGroup): ProgressBar {
    val progressBar = ProgressBar(context, null, android.R.attr.progressBarStyleLarge)
    container.addView(progressBar)
    return progressBar
}