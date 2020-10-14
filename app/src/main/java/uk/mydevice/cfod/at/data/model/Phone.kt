package uk.mydevice.cfod.at.data.model

import com.google.gson.annotations.SerializedName

data class Phone(
    @SerializedName("phoneNumber")
    val phoneNumber: String? = null,
    @SerializedName("onsId")
    val onsId: String? = null
)
