package uk.mydevice.cfod.at.data.pubsub

import com.google.api.core.ApiFuture

interface PubSub {
    fun publish(message: String): ApiFuture<String>?
    fun shutDownPublisher()
}
