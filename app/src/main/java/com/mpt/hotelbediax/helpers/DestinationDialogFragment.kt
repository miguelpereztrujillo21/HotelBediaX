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
import com.mpt.hotelbediax.models.Destination
import java.util.Calendar
import kotlin.random.Random

class DestinationDialogFragment(private val clickListener: OnAddClickListener, private val isEdit: Boolean) : DialogFragment() {

    private var _binding: DialogDestinationBinding? = null
    private val binding get() = _binding!!

    private var destination: Destination? = null

    fun setDestination(destination: Destination) {
        this.destination = destination
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogDestinationBinding.inflate(layoutInflater)
        val spinner: Spinner = binding.dialogSpinner
        val options = arrayOf("City", "Country")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item,options)
        spinner.adapter = adapter

        destination?.let {
            binding.dialogDestinationName.setText(it.name)
            binding.dialogDestinationDescription.setText(it.description)
            binding.dialogDatePicker.setText(it.lastModify)
            val spinnerPosition = adapter.getPosition(it.type)
            spinner.setSelection(spinnerPosition)
        }

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
            .setTitle(if(isEdit) getString(R.string.edit_button) else getString(R.string.dialog_add_button))
            .setPositiveButton(if (isEdit)getString(R.string.edit_button)else getString(R.string.dialog_add_button)) { _, _ ->
                val destination = Destination(
                    destination?.id ?: generateTemporaryId(),
                    binding.dialogDestinationName.text.toString(),
                    binding.dialogDestinationDescription.text.toString(),
                    "",
                    spinner.selectedItem.toString(),
                    binding.dialogDatePicker.text.toString(),
                    true
                )
                clickListener.onAddClick(destination)
                dismiss()
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
    private fun generateTemporaryId(): Int {
        return Random.nextInt(Int.MAX_VALUE)
    }
    interface OnAddClickListener {
        fun onAddClick(destination: Destination)
    }
}