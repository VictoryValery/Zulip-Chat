package com.victoryvalery.tfsproject.data.roomStorage

import androidx.room.Database
import androidx.room.RoomDatabase
import com.victoryvalery.tfsproject.data.roomStorage.models.MessageEntity
import com.victoryvalery.tfsproject.data.roomStorage.models.StreamEntity
import com.victoryvalery.tfsproject.data.roomStorage.models.TopicEntity
import com.victoryvalery.tfsproject.data.roomStorage.models.UserEntity

@Database(
    entities = [
        MessageEntity::class,
        StreamEntity::class,
        TopicEntity::class,
        UserEntity::class
    ], version = 2, exportSchema = false
)
abstract class AppDataBase : RoomDatabase() {

    abstract val zulipDao: ZulipDao

    companion object {
        const val DATABASE_NAME = "ZulipDataBase"
    }

}
