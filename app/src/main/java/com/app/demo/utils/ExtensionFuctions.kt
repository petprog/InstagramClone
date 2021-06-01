/**
 * Created by Taiwo Farinu on 29-May-21
 */

package com.app.demo.utils

import android.content.Context
import android.widget.Toast

fun Context.createShortToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}