package com.equipo2

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.equipo2.model.UserModel
import com.equipo2.utils.AndroidUtil
import com.equipo2.utils.FirebaseUtil
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        if (FirebaseUtil.isLoggedIn() && intent.extras != null) {
            //from notification
            val userId = intent.extras!!.getString("userId")
            FirebaseUtil.allUserCollectionReference().document(userId!!).get()
                .addOnCompleteListener { task: Task<DocumentSnapshot> ->
                    if (task.isSuccessful) {
                        val model = task.result.toObject(
                            UserModel::class.java
                        )
                        val mainIntent = Intent(this, MainActivity::class.java)
                        mainIntent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                        startActivity(mainIntent)
                        val intent = Intent(this, ChatActivity::class.java)
                        AndroidUtil.passUserModelAsIntent(intent, model)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }
                }
        } else {
            runOnUiThread {
                Handler().postDelayed({
                    if (FirebaseUtil.isLoggedIn()) {
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    } else {
                        startActivity(Intent(this@SplashActivity, LoginPhoneNumberActivity::class.java))
                    }
                    finish()
                }, 1000)
            }
        }
    }
}