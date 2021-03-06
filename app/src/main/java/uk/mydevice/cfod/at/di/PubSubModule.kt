package uk.mydevice.cfod.at.di

import android.content.Context
import com.google.auth.oauth2.GoogleCredentials
import com.google.pubsub.v1.ProjectTopicName
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import uk.mydevice.cfod.at.R
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object PubSubModule {

    @Provides
    @Singleton
    fun provideCredentials(@ApplicationContext context: Context): GoogleCredentials =
        GoogleCredentials.fromStream(context.resources?.run { openRawResource(R.raw.creds) })

    @Provides
    @Singleton
    fun provideTopicName(@ApplicationContext context: Context): ProjectTopicName =
        ProjectTopicName.of(
            context.resources.getString(R.string.project_name),
            context.resources.getString(R.string.topic_name)
        )
}
