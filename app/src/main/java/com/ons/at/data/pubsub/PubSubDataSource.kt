package com.ons.at.data.pubsub

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.pubsub.v1.AckReplyConsumer
import com.google.cloud.pubsub.v1.MessageReceiver
import com.google.pubsub.v1.PubsubMessage
import java.io.InputStream

class PubSubDataSource(
    private val googleCredentials: InputStream?,
    val projectName: String,
    val topic: String
):PubSub,MessageReceiver {

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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun receiveMessage(message: PubsubMessage?, consumer: AckReplyConsumer?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}