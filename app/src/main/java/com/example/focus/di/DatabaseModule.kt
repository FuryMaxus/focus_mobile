package com.example.focus.di

import android.content.Context
import androidx.room.Room
import com.example.focus.data.local.FocusDatabase
import com.example.focus.data.local.dao.FocusSessionDao
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
    fun provideFocusDatabase(@ApplicationContext context: Context): FocusDatabase {
        return Room.databaseBuilder(
            context,
            FocusDatabase::class.java,
            "focus_local_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideFocusSessionDao(database: FocusDatabase): FocusSessionDao {
        return database.focusSessionDao()
    }

}