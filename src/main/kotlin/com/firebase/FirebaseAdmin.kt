package com.firebase

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import java.io.FileInputStream
import java.io.InputStream

object FirebaseAdmin {
    private val service : InputStream = FileInputStream("/etc/secrets/service_account.json")

    private val options: FirebaseOptions = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(service))
        .build()

    fun init(): FirebaseApp = FirebaseApp.initializeApp(options)
}