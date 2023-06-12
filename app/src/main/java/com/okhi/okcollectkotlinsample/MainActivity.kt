package com.okhi.okcollectkotlinsample

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.okhi.okcollectkotlinsample.databinding.ActivityMainBinding
import com.okhi.okcollectkotlinsample.fragments.OkhiFragmentActivity
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

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var okhi: OkHi
    private lateinit var okVerify: OkVerify
    private lateinit var user: OkHiUser
    private lateinit var okCollect: OkCollect
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        context = this
        setUpOkhi()
        with(binding){
            startOkCollectActivityBtn.setOnClickListener {
                launchOkhiCollect()
            }

            openFragments.setOnClickListener {
                val i = Intent(this@MainActivity, OkhiFragmentActivity::class.java)
                startActivity(i)
            }
        }
    }

    private fun setUpOkhi(){
        try {
            okhi = OkHi(this)
            okVerify = OkVerify.Builder(this).build()
            val importance = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) NotificationManager.IMPORTANCE_DEFAULT else 3

            OkVerify.init(
                applicationContext, OkHiNotification(
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

            okCollect = OkCollect.Builder(this)
                .withTheme(theme)
                .withConfig(config)
                .build()

        } catch (exception: OkHiException) {
            exception.printStackTrace()
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
                showToast(context, "Address created Successfully for ${user.firstName} with location id ${location.id}")

            }

            override fun onError(e: OkHiException) {
                showToast(context, "Error " + e.message)
            }

            override fun onClose() {
                showToast(context, "User on close OkCollect, success ")
            }
        })
    }

    companion object {
         fun showToast(context: Context, str: String){
            Toast.makeText(context, str, Toast.LENGTH_LONG).show()
        }
    }

}