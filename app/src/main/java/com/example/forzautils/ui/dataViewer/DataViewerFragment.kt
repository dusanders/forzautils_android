package com.example.forzautils.ui.dataViewer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.example.forzautils.R
import com.example.forzautils.ui.dataViewer.dataOptions.DataOptionsFragment
import com.example.forzautils.ui.dataViewer.dataOptions.DataOptionsViewModel

class DataViewerFragment(private val viewModel: DataViewerViewModel) : Fragment(),
    DataOptionsViewModel.Callback {

    private lateinit var dataOptionsViewModel: DataOptionsViewModel
    private lateinit var view: View

    private val dataDisplayObserver: Observer<DataViewerViewModel.DataDisplay> =
        Observer { display ->
            setDisplay(display)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataOptionsViewModel = DataOptionsViewModel(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        view = inflater.inflate(R.layout.fragment_data_viewer, container, false)
        return view
    }

    override fun onResume() {
        super.onResume()
        initializeViewModel()
    }

    override fun onPause() {
        super.onPause()
        destroyViewModel()
    }

    override fun onHpTorqueClick() {
        viewModel.setDataDisplay(DataViewerViewModel.DataDisplay.HP_TORQUE)
    }

    private fun destroyViewModel() {
        viewModel.currentDataDisplay.removeObserver(dataDisplayObserver)
    }

    private fun initializeViewModel() {
        viewModel.currentDataDisplay.observe(this, dataDisplayObserver)
    }

    private fun setDisplay(data: DataViewerViewModel.DataDisplay) {
        var fragment: Fragment = DataOptionsFragment(dataOptionsViewModel)
        when (data) {
            DataViewerViewModel.DataDisplay.OPTIONS_LIST -> {
                // no-op : we default to options list
            }

            DataViewerViewModel.DataDisplay.HP_TORQUE -> {
                fragment = DataOptionsFragment(dataOptionsViewModel)
            }
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.dataViewer_content, fragment)
            .commit()
    }
}