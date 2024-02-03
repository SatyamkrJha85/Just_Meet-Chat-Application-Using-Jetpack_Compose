package com.jetpackcomposethe.justmeet

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent


@Module
@InstallIn(ViewModelComponent::class)
class Hiltmodule {
    @Provides
    fun provideauthentication(): FirebaseAuth=Firebase.auth

    @Provides
    fun provideFirestore(): FirebaseFirestore=Firebase.firestore

    @Provides
    fun provideStorage(): FirebaseStorage = Firebase.storage
}