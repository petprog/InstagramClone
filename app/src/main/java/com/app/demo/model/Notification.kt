/**
 * Created by Taiwo Farinu on 03-Aug-21
 */

package com.app.demo.model

data class Notification(
    var userid: String = "",
    var text: String = "",
    var postid: String = "",
    @field:JvmField
    var isPost: Boolean = false,
)