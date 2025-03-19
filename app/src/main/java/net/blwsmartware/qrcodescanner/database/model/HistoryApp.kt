package  net.blwsmartware.qrcodescanner.database.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import net.blwsmartware.qrcodescanner.model.CreateType
import net.blwsmartware.qrcodescanner.model.QrType
import net.blwsmartware.qrcodescanner.model.ScanType
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "HistoryApp")
data class HistoryApp(
    @PrimaryKey(autoGenerate = true)
    var hisId: Int = 0,
    var scanTime: String = "",
    var scanType: ScanType = ScanType.QRCODE,
    var scanVales: String = "",
    var scanImage: String = "",
    var type: QrType = QrType.TEXT,
    var qrStyle: String ="",
    var qrName: String = "",
    var createType: CreateType = CreateType.CREATE,
) : Parcelable