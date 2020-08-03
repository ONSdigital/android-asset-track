package uk.gov.ons.census.cfod.at.data.pubsub

import android.util.Log
import com.google.api.core.ApiFutureCallback
import com.google.api.core.ApiFutures
import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.pubsub.v1.AckReplyConsumer
import com.google.cloud.pubsub.v1.MessageReceiver
import com.google.cloud.pubsub.v1.Publisher
import com.google.common.util.concurrent.MoreExecutors
import com.google.protobuf.ByteString
import com.google.pubsub.v1.ProjectTopicName
import com.google.pubsub.v1.PubsubMessage
import javax.inject.Inject

class PubSubApi @Inject constructor(
    googleCredentials: GoogleCredentials,
    projectTopicName: ProjectTopicName
) : PubSub, MessageReceiver {

    private val publisher = Publisher
        .newBuilder(projectTopicName)
        .setCredentialsProvider(FixedCredentialsProvider.create(googleCredentials))
        .build()

    private val apiFutureCallback = object : ApiFutureCallback<String> {
        override fun onSuccess(result: String?) {
            Log.d(TAG, "Publish message id : $result")
        }

        override fun onFailure(t: Throwable?) {
            Log.e(TAG, "Publish message failed ${t.toString()}")
        }
    }

    /**
     * Method implementation for subscribe to a topic in pub/sub environment
     */
    override fun subscribe(message: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Method implementation for publish a message to subscribers
     */
    override fun publish(message: String) {
        val pubSubMessage = PubsubMessage
            .newBuilder()
            .setData(ByteString.copyFromUtf8(message))
            .build()
        val messageIdFuture = publisher.publish(pubSubMessage)
        ApiFutures.addCallback(messageIdFuture, apiFutureCallback, MoreExecutors.directExecutor())
    }

    override fun receiveMessage(message: PubsubMessage?, consumer: AckReplyConsumer?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        val TAG = PubSubApi::class.simpleName
    }
}
