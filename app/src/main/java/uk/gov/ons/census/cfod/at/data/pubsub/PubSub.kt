package uk.gov.ons.census.cfod.at.data.pubsub

interface PubSub {
    fun subscribe(message: String)
    fun publish(message: String)
}
