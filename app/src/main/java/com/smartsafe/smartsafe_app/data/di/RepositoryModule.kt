package com.smartsafe.smartsafe_app.data.di

import com.smartsafe.smartsafe_app.data.repository.AuthWithPhoneRepository
import com.smartsafe.smartsafe_app.data.repository.AuthWithPhoneRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class RepositoryModule {
    
    @Binds
    abstract fun bindAuthWithPhoneRepository(
        authWithPhoneRepositoryImpl: AuthWithPhoneRepositoryImpl
    ): AuthWithPhoneRepository
}