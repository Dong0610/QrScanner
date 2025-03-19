package net.blwsmartware.qrcodescanner.ui.main

import androidx.lifecycle.lifecycleScope
import com.dong.baselib.widget.click
import com.dong.baselib.widget.fromColor
import net.blwsmartware.qrcodescanner.adapter.HistoryAdapter
import net.blwsmartware.qrcodescanner.app.viewModel
import net.blwsmartware.qrcodescanner.base.BaseFragment
import net.blwsmartware.qrcodescanner.database.model.HistoryApp
import net.blwsmartware.qrcodescanner.databinding.FragmentHistoryBinding
import net.blwsmartware.qrcodescanner.dialog.DeleteDialog
import net.blwsmartware.qrcodescanner.ui.result.ResultCreateActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HistoryFragment : BaseFragment<FragmentHistoryBinding>(FragmentHistoryBinding::inflate) {
    override fun FragmentHistoryBinding.initView() {
        deleteDialog = DeleteDialog(requireActivity()) {
            historySelect?.let {
                viewModel.deleteHistory(it)
            }
        }
        listItem.adapter = historyAdapter
        lifecycleScope.launch {
            viewModel.dataImpl.allData.collectLatest {
                historyAdapter.submitList(it)
            }
        }
    }

    private var historySelect: HistoryApp? = null

    companion object {
        var listHistorySelect = mutableListOf<HistoryApp>()
    }

    override fun backPress() {
        super.backPress()
        fragmentAttach?.fragmentOnBack()
    }
    private var deleteDialog: DeleteDialog? = null

    private val historyAdapter by lazy {
        HistoryAdapter({
            historySelect=it
            deleteDialog?.show()
        }) { his ->
            viewModel.createHistory.update { his }
            launchActivity<ResultCreateActivity>()
        }
    }

    fun changeStateButton(listHistorySelect: MutableList<HistoryApp>) {
        if (listHistorySelect.size == 0) {
            binding.icDelete.setColorFilter(fromColor("e0e0e0"))
        } else {
            binding.icDelete.setColorFilter(fromColor("FF0000"))
        }
    }

    override fun FragmentHistoryBinding.onClick() {


    }

}