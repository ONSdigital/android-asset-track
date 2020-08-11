package uk.gov.ons.census.cfod.at

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AssetTrackApp : Application(){
    companion object{
        const val SHARED_PREF_FILE_NAME = "Preferences"
    }
}
