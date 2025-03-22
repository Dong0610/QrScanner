package  net.blwsmartware.qrcodescanner.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import net.blwsmartware.qrcodescanner.database.model.HistoryApp
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: HistoryApp)

    @Delete
    suspend fun delete(history: HistoryApp)

    @Query("SELECT * FROM HistoryApp ORDER BY scanTime DESC")
    fun getAllData(): Flow<List<HistoryApp>>

    @Query("SELECT * FROM HistoryApp WHERE scanTime LIKE :date")
    suspend fun getHistoryAppByDate(date: String): MutableList<HistoryApp>

    @Query("SELECT COUNT(*) FROM HistoryApp")
    suspend fun getTotalScan(): Int
    @Query("DELETE FROM HistoryApp WHERE hisId = :hisId")
    suspend fun deleteHistory(hisId: Int):Int
    @Insert
    suspend fun insertWithId(history: HistoryApp): Long
    @Query("UPDATE HistoryApp SET qrName= :currentName WHERE hisId = :saveQrHistory")
   suspend fun updateName(currentName: String, saveQrHistory: Int)
   @Query("UPDATE HistoryApp SET scanImage= :scanImage, qrStyle=:style   WHERE hisId = :hisId")
    suspend fun updateImageHistory(hisId: Int, scanImage: String,style:String)

    @Query("SELECT * FROM HistoryApp WHERE hisId = :toInt")
    suspend fun findHistoryById(toInt: Int): HistoryApp
}


