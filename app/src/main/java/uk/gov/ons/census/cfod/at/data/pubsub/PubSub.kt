package uk.gov.ons.census.cfod.at.data.pubsub

interface PubSub {
    fun publish(message: String): String?
    fun shutDownPublisher()
}
