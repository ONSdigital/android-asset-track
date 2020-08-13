package uk.gov.ons.census.cfod.at

import android.Manifest
import android.app.Activity
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import uk.gov.ons.census.cfod.at.data.account.UserAccountApi
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
        requestPermissions()
        close_button.setOnClickListener{
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
                enqueuePublishWorker()
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
                    enqueuePublishWorker()
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
        val onsId = userAccountApi.getEmail()
        with(sharedPreferences.edit()) {
            putString(getString(R.string.ons_id), onsId)
            commit()
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
            .build()
        WorkManager.getInstance(applicationContext).enqueue(request)
    }

    companion object {
        val TAG: String = MainActivity::class.java.simpleName
        const val PERMISSION_MULTIPLE_REQUEST_CODE = 123
    }
}
