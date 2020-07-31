package com.ons.at.di

import com.ons.at.data.pubsub.PubSub
import com.ons.at.data.pubsub.PubSubApi
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
abstract class NetworkModule {

    @Binds
    @Singleton
    abstract fun bindPubSubApi(pubSubApi: PubSubApi) : PubSub
}
