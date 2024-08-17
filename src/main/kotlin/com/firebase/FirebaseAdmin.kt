package com.firebase

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import java.io.InputStream

object FirebaseAdmin {
    private val service : InputStream = this::class.java.classLoader.getResourceAsStream("service_account.json")

    private val options: FirebaseOptions = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(service))
        .build()

    fun init(): FirebaseApp = FirebaseApp.initializeApp(options)
}