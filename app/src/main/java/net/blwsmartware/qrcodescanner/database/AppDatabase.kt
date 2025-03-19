package  net.blwsmartware.qrcodescanner.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import net.blwsmartware.qrcodescanner.database.model.HistoryApp


@Database(entities = [HistoryApp::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {


    abstract fun history(): HistoryDao
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun init(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "history_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}