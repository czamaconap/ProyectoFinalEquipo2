package com.equipo2


    import android.Manifest
    import android.content.pm.PackageManager
    import android.os.Bundle
    import android.util.Log
    import android.view.LayoutInflater
    import android.view.SurfaceView
    import android.view.View
    import android.view.ViewGroup
    import android.widget.Button
    import android.widget.FrameLayout
    import android.widget.Toast
    import androidx.core.app.ActivityCompat
    import androidx.core.content.ContextCompat
    import androidx.fragment.app.Fragment
    import com.equipo2.agora.media.RtcTokenBuilder2
    import com.equipo2.R
    import io.agora.rtc2.ChannelMediaOptions
    import io.agora.rtc2.Constants
    import io.agora.rtc2.IRtcEngineEventHandler
    import io.agora.rtc2.RtcEngine
    import io.agora.rtc2.RtcEngineConfig
    import io.agora.rtc2.video.VideoCanvas


class VideoCallFragment : Fragment() {

        private  var agoraEngine: RtcEngine? = null
        private lateinit var remoteVideoViewContainer: FrameLayout // Reemplaza FrameLayout con el tipo de tu vista



    private val appId = "5fb926599aeb4ba391c29247cc3b6f71"

    var appCertificate = "b5065fbfa5ed4d8aba0c25de974502b1"
    var expirationTimeInSeconds = 3600
    private val channelName = "christian"

    private var token : String? = null
//    private val token =
//        "007eJxTYDDp3SWzb5uimNhB/V+mL5lvLmFi/xjmUq06+8V/76bHa+YoMJimJVkamZlaWiamJpkkJRpbGiYbWRqZmCcnGyeZpZkbKnYuTG4IZGRgnyjPxMgAgSA+D0NBYkFiZWJyfkpqUTEDAwAi+yGx"

    private val uid = 0
    private var isJoined = false


    private var localSurfaceView: SurfaceView? = null

    private var remoteSurfaceView: SurfaceView? = null


    private val PERMISSION_REQ_ID = 22
    private val REQUESTED_PERMISSIONS = arrayOf<String>(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CAMERA
    )

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {

            val view =  inflater.inflate(R.layout.fragment_videocall, container, false)

            var  join: Button = view.findViewById<Button>(R.id.JoinButton)
            var  leave: Button = view.findViewById<Button>(R.id.LeaveButton)

            join.setOnClickListener {
                joinChannel(it)
            }

            leave.setOnClickListener {
                leaveChannel(it)
            }

            val tokenBuilder = RtcTokenBuilder2()
            val timestamp = (System.currentTimeMillis() / 1000 + expirationTimeInSeconds).toInt()

            println("UID token")
            val result = tokenBuilder.buildTokenWithUid(
                appId, appCertificate,
                channelName, uid, RtcTokenBuilder2.Role.ROLE_PUBLISHER, timestamp, timestamp
            )
            println(result)
            token = result

            if (!checkSelfPermission()) {
                ActivityCompat.requestPermissions(requireActivity(), REQUESTED_PERMISSIONS, PERMISSION_REQ_ID);
            }
            setupVideoSDKEngine(view);

            return view
        }


        override fun onDestroyView() {
            super.onDestroyView()
            agoraEngine?.stopPreview()
            agoraEngine?.leaveChannel()

            Thread {
                RtcEngine.destroy()
            }.start()
        }

        private fun setupVideoSDKEngine(view: View?) {
            try {
                val config = RtcEngineConfig()
                config.mContext = requireContext()
                config.mAppId = appId
                config.mEventHandler = mRtcEventHandler
                agoraEngine = RtcEngine.create(config)

                // By default, the video module is disabled, call enableVideo to enable it.
                agoraEngine?.enableVideo()
            } catch (e: Exception) {
                Log.i("VideoCallFragment", e.toString())
                showMessage(e.toString())
            }
        }

        private val mRtcEventHandler: IRtcEngineEventHandler = object : IRtcEngineEventHandler() {
            override fun onUserJoined(uid: Int, elapsed: Int) {
                showMessage("Remote user joined $uid")
                Log.i("VideoCallFragment", "Remote user joined $uid")
                // Set the remote video view
                requireActivity().runOnUiThread { setupRemoteVideo(uid) }
            }

            override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
                isJoined = true
                showMessage("Joined Channel $channel")
                Log.i("VideoCallFragment","Joined Channel $channel")
            }

            override fun onUserOffline(uid: Int, reason: Int) {
                showMessage("Remote user offline $uid $reason")
                Log.i("VideoCallFragment","Remote user offline $uid $reason")
                requireActivity().runOnUiThread {
                    remoteSurfaceView!!.visibility = View.GONE
                }
            }
        }

        private fun setupRemoteVideo(uid: Int) {

            remoteSurfaceView = SurfaceView(requireContext())
            remoteSurfaceView!!.setZOrderMediaOverlay(true)

           var remote = view?.findViewById<FrameLayout>(R.id.remote_video_view_container)
               remote?.addView(remoteSurfaceView)

            agoraEngine?.setupRemoteVideo(
                VideoCanvas(remoteSurfaceView,
                    VideoCanvas.RENDER_MODE_FIT,
                    uid
                )
            )
            requireActivity().runOnUiThread {
                remote?.visibility = View.VISIBLE
            }
        }

        private fun setupLocalVideo() {
            localSurfaceView = SurfaceView(requireContext())
            localSurfaceView!!.setZOrderMediaOverlay(true)

            var local = view?.findViewById<FrameLayout>(R.id.local_video_view_container)
            local?.addView(localSurfaceView)
            agoraEngine?.setupLocalVideo(
                VideoCanvas(
                    localSurfaceView,
                    VideoCanvas.RENDER_MODE_HIDDEN,
                    0
                )
            )
            requireActivity().runOnUiThread {
                local?.visibility = View.VISIBLE
            }
        }


    fun joinChannel(view: View) {
        if (checkSelfPermission()) {
            val options = ChannelMediaOptions()

            options.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
            options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
            setupLocalVideo()
            localSurfaceView!!.visibility = View.VISIBLE
            agoraEngine!!.startPreview()
            agoraEngine!!.joinChannel(token, channelName, uid, options)
        } else {
            Toast.makeText(requireContext(), "Permissions was not granted", Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun leaveChannel(view: View) {
        if (!isJoined) {
            showMessage("Join a channel first")
        } else {
            agoraEngine!!.leaveChannel()
            showMessage("You left the channel")
            if (remoteSurfaceView != null) remoteSurfaceView!!.visibility = View.GONE
            if (localSurfaceView != null) localSurfaceView!!.visibility = View.GONE
            isJoined = false
        }
    }


        private fun checkSelfPermission(): Boolean {
            return !(ContextCompat.checkSelfPermission(
                requireContext(),
                REQUESTED_PERMISSIONS[0]
            ) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                        requireContext(),
                        REQUESTED_PERMISSIONS[1]
                    ) != PackageManager.PERMISSION_GRANTED)
        }

        private fun showMessage(message: String) {
            requireActivity().runOnUiThread {
                Toast.makeText(
                    requireContext(),
                    message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }