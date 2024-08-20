package com.exchangingcurrencyexchangetask.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.exchangingcurrencyexchangetask.utils.Converters

@Database(entities = [CurrencyRate::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class CurrencyDatabase : RoomDatabase() {
    abstract fun currencyDao(): CurrencyDao

    companion object {
        @Volatile
        private var INSTANCE: CurrencyDatabase? = null

        fun getDatabase(context: Context): CurrencyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CurrencyDatabase::class.java,
                    "currency_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
