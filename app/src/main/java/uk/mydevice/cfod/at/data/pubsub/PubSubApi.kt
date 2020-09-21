package uk.mydevice.cfod.at.data.pubsub

import android.util.Log
import com.google.api.core.ApiFuture
import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.pubsub.v1.Publisher
import com.google.protobuf.ByteString
import com.google.pubsub.v1.ProjectTopicName
import com.google.pubsub.v1.PubsubMessage
import java.lang.Exception
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PubSubApi @Inject constructor(
    private val googleCredentials: GoogleCredentials,
    private val projectTopicName: ProjectTopicName
) : PubSub {

    private var publisher: Publisher? = null

    /**
     * Method implementation for publish a message to subscribers
     */
    override fun publish(message: String): ApiFuture<String>? {
        val pubSubMessage = PubsubMessage
            .newBuilder()
            .setData(ByteString.copyFromUtf8(message))
            .build()
        publisher = Publisher
            .newBuilder(projectTopicName)
            .setCredentialsProvider(FixedCredentialsProvider.create(googleCredentials))
            .build()
        return publisher?.publish(pubSubMessage)
    }

    override fun shutDownPublisher() {
        try {
            publisher?.shutdown()
            publisher?.awaitTermination(1, TimeUnit.MINUTES)
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }

    }

    companion object {
        val TAG = PubSubApi::class.simpleName
    }
}
