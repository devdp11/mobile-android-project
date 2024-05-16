package com.example.android_studio_project.fragment.trip.add_trip

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import com.example.android_studio_project.R
import java.util.*

class CustomDatePickerFragment : DialogFragment() {

    interface OnDateSelectedListener {
        fun onDateSelected(year: Int, month: Int, day: Int)
    }

    var listener: OnDateSelectedListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.datepicker)

        val datePicker: DatePicker = dialog.findViewById(R.id.datePicker)
        val buttonOk: Button = dialog.findViewById(R.id.button_ok)
        val buttonCancel: Button = dialog.findViewById(R.id.button_cancel)

        buttonOk.setOnClickListener {
            val year = datePicker.year
            val month = datePicker.month
            val day = datePicker.dayOfMonth
            listener?.onDateSelected(year, month, day)
            dismiss()
        }

        buttonCancel.setOnClickListener {
            dismiss()
        }

        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}
