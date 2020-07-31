package com.ons.at

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.ons.at.data.model.Phone
import com.ons.at.data.pubsub.PubSub
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var pubSub: PubSub

    @Inject
    lateinit var gSon: Gson

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // todo() below code is for test purpose
        val phone = Phone(
            phoneNumber = "07548975641",
            onsId = "test@gmail.com",
            assetId = "5638129100"
        )
        publish_button.setOnClickListener {
            pubSub.publish(gSon.toJson(phone))
        }
    }
}

