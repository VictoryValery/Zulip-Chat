package com.victoryvalery.tfsproject.di

import android.content.Context
import androidx.room.Room
import com.victoryvalery.tfsproject.data.roomStorage.AppDataBase
import com.victoryvalery.tfsproject.data.roomStorage.AppDataBase.Companion.DATABASE_NAME
import com.victoryvalery.tfsproject.data.roomStorage.ZulipDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RoomModule {

    @Singleton
    @Provides
    fun provideZulipDatabase(context: Context): AppDataBase {
        return Room.databaseBuilder(
            context,
            AppDataBase::class.java,
            DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideZulipDao(zulipDB: AppDataBase): ZulipDao {
        return zulipDB.zulipDao
    }

}
