package com.mpt.hotelbediax.helpers


import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mpt.hotelbediax.R
import com.mpt.hotelbediax.databinding.DialogDestinationBinding
import java.util.Calendar

class DestinationDialogFragment : DialogFragment() {

    private var _binding: DialogDestinationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogDestinationBinding.inflate(layoutInflater)

        val spinner: Spinner = binding.dialogSpinner
        val options = arrayOf("City", "Country")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item,options)
        spinner.adapter = adapter

        val datePickerEditText: EditText = binding.dialogDatePicker
        datePickerEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "${selectedDay}/${selectedMonth + 1}/$selectedYear"
                datePickerEditText.setText(selectedDate)
            }, year, month, day).show()
        }

        return MaterialAlertDialogBuilder(requireContext(), R.style.CustomAlertDialog)
            .setView(binding.root)
            .setTitle("Add Destination")
            .setPositiveButton("Add") { _, _ ->
                
            }
            .setNegativeButton("Cancel") { _, _ ->
                // Handle the negative button action here
            }
            .create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}