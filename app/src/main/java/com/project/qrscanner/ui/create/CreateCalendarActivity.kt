package com.project.qrscanner.ui.create

import com.dong.baselib.lifecycle.LauncherEffect
import com.dong.baselib.lifecycle.get
import com.dong.baselib.lifecycle.mutableLiveData
import com.dong.baselib.lifecycle.set
import com.dong.baselib.widget.afterTextChanged
import com.dong.baselib.widget.click
import com.project.qrscanner.R
import com.project.qrscanner.app.viewModel
import com.project.qrscanner.base.BaseActivity
import com.project.qrscanner.base.BaseDialog
import com.project.qrscanner.builder.setState
import com.project.qrscanner.builder.textViewError
import com.project.qrscanner.databinding.ActivityCreateCalendarBinding
import com.project.qrscanner.databinding.DialogCalendarBinding
import com.project.qrscanner.model.EventModel
import com.project.qrscanner.model.QrType
import com.project.qrscanner.ui.result.ResultCreateActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CreateCalendarActivity :
    BaseActivity<ActivityCreateCalendarBinding>(ActivityCreateCalendarBinding::inflate) {

    private val eventError = mutableLiveData(" ")
    private val startDateError = mutableLiveData(" ")
    private val endDateError = mutableLiveData(" ")
    private val locationError = mutableLiveData(" ")

    private var type = "start"
    private var startDate: Date = Calendar.getInstance().time
    private var endDate: Date? = null
    override fun backPressed() {
        finish()
    }

    override fun initialize() {
        eventError.textViewError(binding.txtErrorEvent, this)
        startDateError.textViewError(binding.txtErrorStart, this)
        endDateError.textViewError(binding.txtErrorEnd, this)
        locationError.textViewError(binding.txtErrorLoc, this)
    }

    override fun ActivityCreateCalendarBinding.onClick() {
        btnBack.click {
            backPressed()
        }
        btnCreate.click {
            hideKeyboard()
            if (btnCreate.isActivated) {
                val eventData = EventModel(
                    etEvent.text.toString(),
                    etStartDate.text.toString(),
                    etEndate.text.toString(),
                    etLocation.text.toString(), etNote.text.toString()
                ).toQRCodeString()
                viewModel.createQrWithValue(eventData, QrType.EVENT) {
                    launchActivity<ResultCreateActivity>()
                }
            }
        }
        llStart.click {
            type = "start"
            calendarDialog.showDialog(startDate)
        }
        llEndate.click {
            type = "end"
            calendarDialog.showDialog(endDate ?: startDate)
        }
    }
    fun Date.toDateFormat(pattern: String): String {
        val formatter = SimpleDateFormat(pattern, Locale.getDefault())
        return formatter.format(this)
    }
    val calendarDialog by lazy {
        CalendarDialog { selectedDate ->
            if (type == "start") {
                startDate = selectedDate
                binding.etStartDate.setText(selectedDate.toDateFormat("yyyy-MM-dd"))
                // Reset endDate if it's before the new startDate
                if (endDate != null && endDate!!.before(startDate)) {
                    endDate = null
                    binding.etEndate.setText("")
                    endDateError.set(getString(R.string.end_date_after_start_date))
                }
            } else {
                if (selectedDate.before(startDate)) {
                    endDateError.set(getString(R.string.end_date_after_start_date))
                } else {
                    endDate = selectedDate
                    binding.etEndate.setText(selectedDate.toDateFormat("yyyy-MM-dd"))
                    endDateError.set(" ")
                }
            }
        }
    }

    inner class CalendarDialog(var callback: (Date) -> Unit = { _ -> }) :
        BaseDialog<DialogCalendarBinding>(this@CreateCalendarActivity) {
        override fun setBinding() = DialogCalendarBinding.inflate(layoutInflater)
        private var lastTimeSet: Date? = null

        override fun DialogCalendarBinding.initView() {
            binding.tvAgree.click {
                callback(Date(calendarView.date))
                dismiss()
            }
            tvStay.click {
                lastTimeSet?.let { it1 -> callback(it1) }
                dismiss()
            }
        }

        fun showDialog(time: Date) {
            if (isShowing) {
                dismiss()
            }
            lastTimeSet = time
            binding.calendarView.setDate(time.time, true, true)
            show()
        }
    }

    override fun ActivityCreateCalendarBinding.setData() {
        etEvent.afterTextChanged {
            if (it.trim().isEmpty()) {
                eventError.set(getString(R.string.require_value_enter))
            } else {
                eventError.set(" ")
            }
        }
        etStartDate.afterTextChanged {
            if (it.trim().isEmpty()) {
                startDateError.set(getString(R.string.require_value_enter))
            } else {
                startDateError.set(" ")
            }
        }
        etEndate.afterTextChanged {
            if (it.trim().isEmpty()) {
                endDateError.set(getString(R.string.require_value_enter))
            } else {
                endDateError.set(" ")
            }
        }
        etLocation.afterTextChanged {
            if (it.trim().isEmpty()) {
                locationError.set(getString(R.string.require_value_enter))
            } else {
                locationError.set(" ")
            }
        }

        LauncherEffect(eventError, startDateError, endDateError, locationError) {
            val isEventErrorEmpty = eventError.get() == " "
            val isStartDateErrorEmpty = startDateError.get() == " "
            val isEndDateErrorEmpty = endDateError.get() == " "
            val isLocationErrorEmpty = locationError.get() == " "

            btnCreate.setState(isEventErrorEmpty && isStartDateErrorEmpty && isEndDateErrorEmpty && isLocationErrorEmpty)
        }
    }
}