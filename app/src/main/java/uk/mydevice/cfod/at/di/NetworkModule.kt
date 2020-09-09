package uk.mydevice.cfod.at.di

import uk.mydevice.cfod.at.data.pubsub.PubSub
import uk.mydevice.cfod.at.data.pubsub.PubSubApi
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
