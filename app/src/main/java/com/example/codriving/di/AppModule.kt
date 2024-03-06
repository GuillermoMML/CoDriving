package com.example.codriving.di

import com.example.codriving.LoginPage.ui.LoginViewModel
import com.example.codriving.data.repository.CarRepository
import com.example.codriving.data.repository.FirebaseAuthRepository
import com.example.codriving.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
    fun provideCarRepository(): CarRepository {
        return CarRepository()
    }

    @Provides
    fun provideLoginViewModel(repository: FirebaseAuthRepository): LoginViewModel {
        return LoginViewModel(repository)
    }
}
