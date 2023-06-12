package com.okhi.okcollectkotlinsample.fragments

import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.okhi.okcollectkotlinsample.MainActivity
import com.okhi.okcollectkotlinsample.databinding.FragmentOkhiBinding
import io.okhi.android_core.OkHi
import io.okhi.android_core.models.OkHiException
import io.okhi.android_core.models.OkHiLocation
import io.okhi.android_core.models.OkHiUser
import io.okhi.android_okcollect.OkCollect
import io.okhi.android_okcollect.callbacks.OkCollectCallback
import io.okhi.android_okcollect.utilities.OkHiConfig
import io.okhi.android_okcollect.utilities.OkHiTheme
import io.okhi.android_okverify.OkVerify
import io.okhi.android_okverify.models.OkHiNotification

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private lateinit var okhi: OkHi
    private lateinit var okVerify: OkVerify
    private lateinit var user: OkHiUser
    private lateinit var okCollect: OkCollect
    private lateinit var context: Context


    private val binding: FragmentOkhiBinding by lazy {
        FragmentOkhiBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        context = this.requireContext()
        try {
            okhi = OkHi(this.requireActivity() as AppCompatActivity)
            okVerify = OkVerify.Builder(this.requireActivity() as Activity).build()
            val importance = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) NotificationManager.IMPORTANCE_DEFAULT else 3
            OkVerify.init(
                this.requireActivity().applicationContext, OkHiNotification(
                    "OkHi RnD Verification",
                    "We're currently verifying your address. This won't take long",
                    "OkHi",
                    "OkHi Address Verification",
                    "Alerts related to any address verification updates",
                    importance,
                    1,  // notificationId
                    2 // notification request code
                )
            )
            val theme: OkHiTheme = OkHiTheme.Builder("#008080")
                .setAppBarLogo("https://cdn.okhi.co/icon.png")
                .setAppBarColor("#008080")
                .build()

            // configure any optional features you'd like
            val config: OkHiConfig = OkHiConfig.Builder()
                .withStreetView()
                .build()

            okCollect = OkCollect.Builder(this.requireActivity() as Activity)
                .withTheme(theme)
                .withConfig(config)
                .build()

        } catch (exception: OkHiException) {
            exception.printStackTrace()
        }

        binding.launchBtn.setOnClickListener {
            launchOkhiCollect()
        }
    }

    private fun launchOkhiCollect() {
        // define a user
        user = OkHiUser.Builder("+254712288371")
            .withFirstName("Granson")
            .withLastName("Oyombe")
            .withEmail("oyombegranson@gmail.com")
            .build()


        okCollect.launch(user, object : OkCollectCallback<OkHiUser, OkHiLocation> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onSuccess(user: OkHiUser, location: OkHiLocation) {

                MainActivity.showToast(
                    context,
                    "Address created Successfully for ${user.firstName} with location id ${location.id}"
                )

            }

            override fun onError(e: OkHiException) {
                MainActivity.showToast(context, "Error " + e.message)
            }

            override fun onClose() {
                MainActivity.showToast(context, "User on close OkCollect, success ")
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }
}