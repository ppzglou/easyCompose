package gr.sppzglou.easycompose.room

import androidx.room.Room
import androidx.room.RoomDatabase
import gr.sppzglou.easycompose.sharedApplication

actual inline fun <reified T: RoomDatabase> getDatabaseBuilder(dbName: String): RoomDatabase.Builder<T> {
    val appContext = sharedApplication.applicationContext
    val dbFile = appContext.getDatabasePath("$dbName.db")
    return Room.databaseBuilder<T>(
        context = appContext,
        name = dbFile.absolutePath
    )
}