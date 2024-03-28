package com.example.codriving.di

import com.example.codriving.LoginPage.ui.LoginViewModel
import com.example.codriving.data.repository.FirebaseAuthRepository
import com.example.codriving.data.repository.FirebaseStorageRepository
import com.example.codriving.data.repository.FirebaseStorageRepositoryImpl
import com.example.codriving.data.repository.UploadCarRepository
import com.example.codriving.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideUserRepository(firestore: FirebaseFirestore): UserRepository {
        return UserRepository(firestore)
    }
    @Provides
    @Singleton
    fun provideAuthRepository(auth: FirebaseAuth): FirebaseAuthRepository
    {
        return FirebaseAuthRepository(auth)
    }
    @Provides
    @Singleton
    fun provideUploadCarRepository(firestore: FirebaseFirestore,firebaseAuthRepository: FirebaseAuthRepository): UploadCarRepository {
        return UploadCarRepository(firestore,firebaseAuthRepository)
    }

    @Provides
    fun provideLoginViewModel(repository: FirebaseAuthRepository): LoginViewModel {
        return LoginViewModel(repository)
    }


    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return Firebase.storage
    }
    @Provides
    @Singleton
    fun provideFirebaseStorageRepository(storage: FirebaseStorage): FirebaseStorageRepository {
        return FirebaseStorageRepositoryImpl(storage)
    }


}
