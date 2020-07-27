package com.ons.at.data.pubsub

interface PubSub {
    fun subscribe(message: String)
    fun publish(message: String)
}