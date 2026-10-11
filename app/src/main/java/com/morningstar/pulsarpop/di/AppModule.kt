package com.morningstar.pulsarpop.di

import android.content.Context
import androidx.room.Room
import com.morningstar.pulsarpop.data.PreferencesRepository
import com.morningstar.pulsarpop.data.room.GameDao
import com.morningstar.pulsarpop.data.room.PulsarDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePreferencesRepository(
        @ApplicationContext context: Context
    ): PreferencesRepository {
        return PreferencesRepository(context)
    }

    @Provides
    @Singleton
    fun providePulsarDatabase(
        @ApplicationContext context: Context
    ): PulsarDatabase {
        return Room.databaseBuilder(
            context,
            PulsarDatabase::class.java,
            "pulsar_pop.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideGameDao(
        database: PulsarDatabase
    ): GameDao {
        return database.gameDao()
    }
}
