package uk.mydevice.cfod.at

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import uk.mydevice.cfod.at.data.model.Phone
import uk.mydevice.cfod.at.data.pubsub.PubSub

class PublishWorker @WorkerInject constructor(
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val pubSub: PubSub,
    private val gSon: Gson,
    private val sharedPreferences: SharedPreferences,
    private val telephonyManager: TelephonyManager,
    private val firebaseAnalytics: FirebaseAnalytics
) : Worker(context, workerParams) {

    @SuppressLint("MissingPermission")
    override fun doWork(): Result {
        return try {
            val phoneNumber = telephonyManager.line1Number
            val email =
                sharedPreferences.getString(context.resources.getString(R.string.ons_id), "")
            setUserPropertyAnalytics(email, phoneNumber)
            if (phoneNumber.isNullOrEmpty()) {
                setSubmitEventAnalytics(State.RETRIED)
                return Result.retry()
            }
            val phone = Phone(phoneNumber = phoneNumber, onsId = email)
            val sPhone = gSon.toJson(phone)
            val messageId = pubSub.publish(sPhone)
            Log.v(TAG, "published with messageId :$messageId")
            pubSub.shutDownPublisher()
            setSubmitEventAnalytics(State.SUBMITTED)
            return Result.success()
        } catch (throwable: Throwable) {
            Log.e(TAG, throwable.message.toString())
            setSubmitEventAnalytics(State.FAILURE)
            Result.failure()
        }
    }

    private fun setUserPropertyAnalytics(onsId: String?, phoneNumber: String) {
        firebaseAnalytics.setUserId(onsId)
        firebaseAnalytics.setUserProperty("PhoneNumber", phoneNumber)
    }

    private fun setSubmitEventAnalytics(state: State) {
        firebaseAnalytics.logEvent("device_info", Bundle().apply {
            putString(SUBMIT_STATE, state.state)
        })
    }

    companion object {
        val TAG: String = PublishWorker::class.java.simpleName
        const val SUBMIT_STATE: String = "state"
    }
}
