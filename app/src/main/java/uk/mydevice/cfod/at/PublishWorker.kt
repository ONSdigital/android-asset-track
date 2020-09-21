package uk.mydevice.cfod.at

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.telephony.TelephonyManager
import android.util.Log
import androidx.concurrent.futures.CallbackToFutureAdapter
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.google.api.core.ApiFutureCallback
import com.google.api.core.ApiFutures
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.google.gson.Gson
import uk.mydevice.cfod.at.data.model.Phone
import uk.mydevice.cfod.at.data.pubsub.PubSub

class PublishWorker @WorkerInject constructor(
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val pubSub: PubSub,
    private val gSon: Gson,
    private val sharedPreferences: SharedPreferences,
    private val telephonyManager: TelephonyManager
) : ListenableWorker(context, workerParams) {

    @SuppressLint("MissingPermission")
    override fun startWork(): ListenableFuture<Result> {
        val phoneNumber = telephonyManager.line1Number
        val email =
            sharedPreferences.getString(context.resources.getString(R.string.ons_id), "")

        if (phoneNumber.isNullOrEmpty()) {
            return CallbackToFutureAdapter.getFuture { it.set(Result.retry()) }
        }

        val phone = Phone(phoneNumber = phoneNumber, onsId = email)
        val sPhone = gSon.toJson(phone)
        val apiFuture = pubSub.publish(sPhone)

        return CallbackToFutureAdapter.getFuture { completer ->
            val callback = object : ApiFutureCallback<String> {
                override fun onFailure(t: Throwable?) {
                    Log.e(TAG, t.toString())
                    completer.set(Result.retry())
                }

                override fun onSuccess(result: String?) {
                    Log.d(TAG, "published with messageId :$result")
                    completer.set(Result.success())
                    pubSub.shutDownPublisher()
                }
            }

            ApiFutures.addCallback(apiFuture, callback, MoreExecutors.directExecutor())
            return@getFuture callback
        }
    }

    companion object {
        val TAG: String = PublishWorker::class.java.simpleName
    }
}