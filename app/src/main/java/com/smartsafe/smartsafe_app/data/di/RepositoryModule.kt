package com.smartsafe.smartsafe_app.data.di

import com.smartsafe.smartsafe_app.data.repository.authWithPhone.AuthWithPhoneRepository
import com.smartsafe.smartsafe_app.data.repository.authWithPhone.AuthWithPhoneRepositoryImpl
import com.smartsafe.smartsafe_app.data.repository.box.BoxRepository
import com.smartsafe.smartsafe_app.data.repository.box.BoxRepositoryImpl
import com.smartsafe.smartsafe_app.data.repository.history.HistoryRepository
import com.smartsafe.smartsafe_app.data.repository.history.HistoryRepositoryImpl
import com.smartsafe.smartsafe_app.data.repository.user.UserRepository
import com.smartsafe.smartsafe_app.data.repository.user.UserRepositoryImpl
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

    @Binds
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    abstract fun bindBoxRepository(
        boxRepositoryImpl: BoxRepositoryImpl
    ): BoxRepository

    @Binds
    abstract fun bindHistoryRepository(
        historyRepositoryImpl: HistoryRepositoryImpl
    ): HistoryRepository
}