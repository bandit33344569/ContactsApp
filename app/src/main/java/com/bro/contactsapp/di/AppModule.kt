package com.bro.contactsapp.di

import android.content.ContentResolver
import com.bro.contactsapp.data.local.ContactLocalDataSource
import com.bro.contactsapp.data.repository.ContactRepositoryImpl
import com.bro.contactsapp.domain.repository.ContactRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideContactLocalDataSource(
        contentResolver: ContentResolver
    ): ContactLocalDataSource {
        return ContactLocalDataSource(contentResolver)
    }

    @Provides
    fun provideContactRepository(
        localDataSource: ContactLocalDataSource
    ): ContactRepository {
        return ContactRepositoryImpl(localDataSource)
    }

    @Provides
    fun provideContentResolver(app: android.app.Application): ContentResolver {
        return app.contentResolver
    }

}