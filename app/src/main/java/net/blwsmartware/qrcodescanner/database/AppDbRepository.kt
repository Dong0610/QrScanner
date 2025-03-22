package  net.blwsmartware.qrcodescanner.database

import androidx.lifecycle.LiveData
import net.blwsmartware.qrcodescanner.database.model.HistoryApp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class AppDbRepository(
    private val historyDao: HistoryDao
) {
    suspend fun insert(yourEntity: HistoryApp) {
        historyDao.insert(yourEntity)
    }

    suspend fun insertWithId(history: HistoryApp): Long {
        return historyDao.insertWithId(history)
    }


    suspend fun deleteHistory(item: HistoryApp) {
        historyDao.delete(item)
    }

    suspend fun removeHistory(item: Int) = historyDao.deleteHistory(item)
    suspend fun findHistoryById(toInt: Int) = historyDao.findHistoryById(toInt)


    val allData: Flow<List<HistoryApp>> = historyDao.getAllData()
}