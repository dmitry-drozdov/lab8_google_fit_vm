package com.example.lab8_google_fit.ui.main

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.format.DateFormat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.Observer
import com.example.lab8_google_fit.MainActivity
import com.example.lab8_google_fit.R
import com.example.lab8_google_fit.data.StepsData
import java.time.LocalDateTime

class MainFragment : Fragment(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    companion object {
        fun newInstance() = MainFragment()
    }

    var myDay = 0
    var myMonth: Int = 0
    var myYear: Int = 0

    var myDateTime: LocalDateTime? = null
    var myStepsCount: Int? = null

    private lateinit var viewModel: MainViewModel
    private lateinit var mainActivity: MainActivity

    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        // TODO: Use the ViewModel
        mainActivity = this.activity as MainActivity
        viewModel.init(mainActivity)
        viewModel.getSteps(mainActivity)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView = mainActivity.findViewById(R.id.steps_list_view)

        val nameObserver = Observer<MutableList<StepsData>> { newList ->
            val adapter = MyAdapter(requireContext(), newList)
            listView.adapter = adapter
        }

        viewModel.stepsListLive.observe(viewLifecycleOwner, nameObserver)

        mainActivity.findViewById<TextView>(R.id.selectedDateTime).setOnClickListener {
            val calendar: Calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                mainActivity,
                this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
            )
            datePickerDialog.show()
        }

        mainActivity.findViewById<SeekBar>(R.id.seekBar)
            .setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    myStepsCount = progress
                    updateUI()
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                }
            })
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        myDay = dayOfMonth
        myYear = year
        myMonth = month + 1
        val calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(
            mainActivity,
            this,
            calendar.get(Calendar.HOUR),
            0,
            DateFormat.is24HourFormat(mainActivity),
        )
        timePickerDialog.show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        myDateTime = LocalDateTime.of(myYear, myMonth, myDay, hourOfDay, 0, 0)
        updateUI()
    }

    private fun updateUI() {
        var title = myDateTime?.toString() ?: "Select date and time"
        if (myStepsCount != null) {
            title += " — $myStepsCount steps"
        }
        mainActivity.findViewById<TextView>(R.id.selectedDateTime).text = title
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

}