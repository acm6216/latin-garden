package me.qingshu.latin.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private lateinit var appDatabase: AppDatabase

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        appDatabase = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "latins.db"
        ).fallbackToDestructiveMigration()
            .addCallback(object : RoomDatabase.Callback() {}).build()
        return appDatabase
    }

    @Provides
    fun provideTextDao(appDatabase: AppDatabase): PlantDao {
        return appDatabase.plantDao()
    }

}