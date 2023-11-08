package com.equipo2.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.equipo2.model.UserModel

object AndroidUtil {
    @JvmStatic
    fun showToast(context: Context?, message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    @JvmStatic
    fun passUserModelAsIntent(intent: Intent, model: UserModel?) {

        model.let {
            intent.putExtra("username", it?.username)
            intent.putExtra("phone", it?.phoneNumber)
            intent.putExtra("userId", it?.userId)
            intent.putExtra("fcmToken", it?.fcmToken)
        }
    }

    @JvmStatic
    fun getUserModelFromIntent(intent: Intent): UserModel {
        return  UserModel(
            username = intent.getStringExtra("username"),
            phoneNumber = intent.getStringExtra("phone"),
            userId = intent.getStringExtra("userId"),
            fcmToken = intent.getStringExtra("fcmToken")
        )
    }

    @JvmStatic
    fun setProfilePic(context: Context?, imageUri: Uri?, imageView: ImageView?) {
        Glide.with(context!!).load(imageUri).apply(RequestOptions.circleCropTransform()).into(
            imageView!!
        )
    }
}