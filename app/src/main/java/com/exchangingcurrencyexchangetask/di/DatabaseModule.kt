package com.exchangingcurrencyexchangetask.di

import android.content.Context
import androidx.room.Room
import com.exchangingcurrencyexchangetask.data.room.CurrencyDao
import com.exchangingcurrencyexchangetask.data.room.CurrencyDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): CurrencyDatabase {
        return Room.databaseBuilder(
            appContext,
            CurrencyDatabase::class.java,
            "currency_db"
        ).build()
    }

    @Provides
    fun provideCurrencyDao(database: CurrencyDatabase): CurrencyDao {
        return database.currencyDao()
    }

    @Provides
    fun provideContext(application: Context): Context {
        return application
    }
}
