package uk.gov.ons.census.cfod.at

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.util.Linkify
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.work.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import uk.gov.ons.census.cfod.at.data.account.UserAccountApi
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var telephonyManager: TelephonyManager

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var userAccountApi: UserAccountApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Linkify.addLinks(infoTextView, Linkify.PHONE_NUMBERS)
        requestPermissions()
        checkBatteryOptimization()
        close_button.setOnClickListener {
            closeApp()
        }
    }

    /**
     * close the app and set result ok  for zero touch enrollment
     */
    private fun closeApp() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    /**
     * this is for  requesting runtime dangerous permissions ,
     * for reading phone number we need READ_PHONE_STATE permission
     */
    private fun requestPermissions() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_STATE
            ),
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            )
            -> {
                readPhoneNumber()
                readOnsId()
            }
            else -> {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_CONTACTS
                    ),
                    PERMISSION_MULTIPLE_REQUEST_CODE
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_MULTIPLE_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                ) {
                    readPhoneNumber()
                    readOnsId()
                } else {
                    Toast.makeText(
                        this,
                        "You can't get phone number and user e-email by denying permissions",
                        Toast.LENGTH_LONG
                    ).show()
                }
                return
            }
        }
    }

    /***
     * this will read phone number from Android device after getting required runtime permission
     */
    private fun readPhoneNumber() {
        try {
            val phoneNumber = telephonyManager.line1Number
            with(sharedPreferences.edit()) {
                putString(getString(R.string.phone_number), phoneNumber)
                commit()
            }
        } catch (se: SecurityException) {
            Log.e(TAG, se.toString())
        }
    }

    /**
     * the user id is Google e-mail address as onsId
     */
    private fun readOnsId() {
        val onsId = sharedPreferences.getString(getString(R.string.ons_id), "")
        if (!onsId.isNullOrEmpty()) {
            infoTextView.text = getString(R.string.info_text, onsId)
        } else {
            val userAccount = userAccountApi.getEmail()
            infoTextView.text = getString(R.string.info_text, userAccount)
            with(sharedPreferences.edit()) {
                putString(getString(R.string.ons_id), userAccount)
                commit()
            }
        }
    }

    /**
     * we have to  publish our data within correct conditions , if there is  network ,etc...
     */
    private fun enqueuePublishWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val request = OneTimeWorkRequestBuilder<PublishWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.LINEAR, 600, TimeUnit.SECONDS)
            .build()
        WorkManager.getInstance(applicationContext).enqueue(request)
    }

    /**
     * we need  ignore battery optimization for our app. Else it is not running at the background
     */
    private fun checkBatteryOptimization() {
        val intent = Intent()
        val packageName = packageName
        val pm =
            getSystemService(Context.POWER_SERVICE) as PowerManager
        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            intent.data = Uri.parse("package:$packageName")
            startActivityForResult(intent, IGNORE_BATTERY_OPTIMIZATION_REQUEST)
        } else {
            enqueuePublishWorker()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == IGNORE_BATTERY_OPTIMIZATION_REQUEST) {
            val pm =
                getSystemService(Context.POWER_SERVICE) as PowerManager
            val isIgnoringBatteryOptimizations =
                pm.isIgnoringBatteryOptimizations(packageName)
            if (isIgnoringBatteryOptimizations) {
                enqueuePublishWorker()
            } else {
                Toast.makeText(this, getString(R.string.allow_run_in_bg_text), Toast.LENGTH_SHORT)
                    .show()
                checkBatteryOptimization()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        val TAG: String = MainActivity::class.java.simpleName
        const val PERMISSION_MULTIPLE_REQUEST_CODE = 123
        const val IGNORE_BATTERY_OPTIMIZATION_REQUEST = 124
    }
}
