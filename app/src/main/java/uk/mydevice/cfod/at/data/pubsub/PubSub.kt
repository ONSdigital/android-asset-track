package uk.mydevice.cfod.at.data.pubsub

interface PubSub {
    fun publish(message: String): String?
    fun shutDownPublisher()
}
