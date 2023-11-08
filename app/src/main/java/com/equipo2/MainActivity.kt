package com.equipo2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.equipo2.utils.FirebaseUtil
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var searchButton: ImageButton
    private lateinit var chatFragment: ChatFragment
    private lateinit var profileFragment: ProfileFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        chatFragment = ChatFragment()
        profileFragment = ProfileFragment()
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        searchButton = findViewById(R.id.main_search_btn)
        searchButton.setOnClickListener(View.OnClickListener { v: View? ->
            startActivity(
                Intent(
                    this@MainActivity,
                    SearchUserActivity::class.java
                )
            )
        })
        bottomNavigationView.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener { item ->
            if (item.itemId == R.id.menu_chat) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frame_layout, chatFragment).commit()
            }
            if (item.itemId == R.id.menu_profile) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frame_layout, profileFragment).commit()
            }
            true
        })
        bottomNavigationView.selectedItemId = R.id.menu_chat
        fCMToken
    }

    private val fCMToken: Unit
        get() {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task: Task<String?> ->
                if (task.isSuccessful) {
                    val token = task.result
                    FirebaseUtil.currentUserDetails().update("fcmToken", token)
                }
            }
        }
}