package com.equipo2

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.equipo2.model.UserModel
import com.equipo2.utils.AndroidUtil
import com.equipo2.utils.FirebaseUtil
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.messaging.FirebaseMessaging

class ProfileFragment : Fragment() {
    private lateinit var profilePic: ImageView
    private lateinit var usernameInput: EditText
    private lateinit var phoneInput: EditText
    private lateinit var updateProfileBtn: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var logoutBtn: TextView
    private var currentUserModel: UserModel? = null
    private lateinit var imagePickLauncher: ActivityResultLauncher<Intent>
    private lateinit var selectedImageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imagePickLauncher = registerForActivityResult<Intent, ActivityResult>(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data?.data != null) {
                    selectedImageUri = data.data!!
                    AndroidUtil.setProfilePic(context, selectedImageUri, profilePic)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        profilePic = view.findViewById(R.id.profile_image_view)
        usernameInput = view.findViewById(R.id.profile_username)
        phoneInput = view.findViewById(R.id.profile_phone)
        updateProfileBtn = view.findViewById(R.id.profle_update_btn)
        progressBar = view.findViewById(R.id.profile_progress_bar)
        logoutBtn = view.findViewById(R.id.logout_btn)
        userData
        updateProfileBtn.setOnClickListener { updateBtnClick() }
        logoutBtn.setOnClickListener { v: View? ->
            FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    FirebaseUtil.logout()
                    val intent = Intent(context, SplashActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            }
        }
        profilePic.setOnClickListener {
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512, 512)
                .createIntent { intent: Intent ->
                    imagePickLauncher.launch(intent)
                    null
                }
        }
        return view
    }

    private fun updateBtnClick() {
        val newUsername = usernameInput.text.toString()
        if (newUsername.isEmpty() || newUsername.length < 3) {
            usernameInput.error = "Username length should be at least 3 chars"
            return
        }
        currentUserModel?.username = newUsername
        setInProgress(true)

        selectedImageUri.let {
            FirebaseUtil.getCurrentProfilePicStorageRef().putFile(selectedImageUri!!)
                .addOnCompleteListener { updateToFirestore() }
        }
    }

    private fun updateToFirestore() {
        FirebaseUtil.currentUserDetails().set(currentUserModel!!)
            .addOnCompleteListener { task: Task<Void?> ->
                setInProgress(false)
                if (task.isSuccessful) {
                    AndroidUtil.showToast(context, "Updated successfully")
                } else {
                    AndroidUtil.showToast(context, "Updated failed")
                }
            }
    }

    private val userData: Unit
        get() {
            setInProgress(true)
            FirebaseUtil.getCurrentProfilePicStorageRef().downloadUrl
                .addOnCompleteListener { task: Task<Uri?> ->
                    if (task.isSuccessful) {
                        val uri: Uri? = task.result
                        AndroidUtil.setProfilePic(context, uri, profilePic)
                    }
                }
            FirebaseUtil.currentUserDetails().get().addOnCompleteListener { task ->
                setInProgress(false)
                currentUserModel = task.result?.toObject(UserModel::class.java)
                currentUserModel.let {
                    usernameInput.setText(it?.username)
                    phoneInput.setText(it?.phoneNumber)
                }
            }
        }

    private fun setInProgress(inProgress: Boolean) {
        takeIf { inProgress }
            .apply {
                progressBar.visibility = View.VISIBLE
                updateProfileBtn.visibility = View.GONE
            }?:run {
            progressBar.visibility = View.GONE
            updateProfileBtn.visibility = View.VISIBLE
        }
    }
}