package uk.mydevice.cfod.at.di

import android.content.Context
import android.content.SharedPreferences
import android.telephony.TelephonyManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import uk.mydevice.cfod.at.AssetTrackApp
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object UtilsModule {

    @Provides
    @Singleton
    fun provideGSon(): Gson = GsonBuilder().create()

    @Provides
    @Singleton
    fun provideTelephonyManager(@ApplicationContext context: Context): TelephonyManager =
        context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    @Provides
    @Singleton
    fun provideSharedPreference(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(
            AssetTrackApp.SHARED_PREF_FILE_NAME,
            Context.MODE_PRIVATE
        )
    }
}
