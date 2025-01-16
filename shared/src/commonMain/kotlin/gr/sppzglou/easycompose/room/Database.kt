package gr.sppzglou.easycompose.room

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

expect inline fun <reified T : RoomDatabase> getDatabaseBuilder(dbName: String): RoomDatabase.Builder<T>

inline fun <reified T : RoomDatabase> getDatabase(dbName: String = "room") = getDatabaseBuilder<T>(dbName)
    .setDriver(BundledSQLiteDriver())
    .fallbackToDestructiveMigration(true)
    .setQueryCoroutineContext(Dispatchers.IO)
    .build()


interface ClearAllTablesWorkaround {
    fun clearAllTables(): Unit {}
}