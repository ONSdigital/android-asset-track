package uk.gov.ons.census.cfod.at

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.telephony.TelephonyManager
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import uk.gov.ons.census.cfod.at.data.model.Phone
import uk.gov.ons.census.cfod.at.data.pubsub.PubSub

class PublishWorker @WorkerInject constructor(
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val pubSub: PubSub,
    private val gSon: Gson,
    private val sharedPreferences: SharedPreferences,
    private val telephonyManager: TelephonyManager
) : Worker(context, workerParams) {

    @SuppressLint("MissingPermission")
    override fun doWork(): Result {
        return try {
            val phoneNumber = telephonyManager.line1Number
            val email =
                sharedPreferences.getString(context.resources.getString(R.string.ons_id), "")
            if (phoneNumber.isNullOrEmpty()) {
                return Result.retry()
            }
            val phone = Phone(phoneNumber = phoneNumber, onsId = email)
            val sPhone = gSon.toJson(phone)
            val messageId = pubSub.publish(sPhone)
            Log.v(TAG, "published with messageId :$messageId")
            return Result.success()
        } catch (throwable: Throwable) {
            Log.v(TAG, throwable.message.toString())
            Result.failure()
        }
    }

    companion object {
        val TAG: String = PublishWorker::class.java.simpleName
    }
}