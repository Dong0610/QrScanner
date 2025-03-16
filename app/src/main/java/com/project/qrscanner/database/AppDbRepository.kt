package  com.project.qrscanner.database

import androidx.lifecycle.LiveData
import com.project.qrscanner.database.model.HistoryApp

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

    val allData: LiveData<List<HistoryApp>> = historyDao.getAllData()
}