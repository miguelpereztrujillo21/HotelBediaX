package com.mpt.hotelbediax.helpers


import android.app.DatePickerDialog
import android.app.Dialog

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.mpt.hotelbediax.R
import com.mpt.hotelbediax.databinding.DialogDestinationBinding
import com.mpt.hotelbediax.models.Destination
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
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
        setUpComponets()

        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.CustomAlertDialog)
            .setView(binding.root)
            .setTitle(if (isEdit) getString(R.string.dialog_title_edit_destination) else getString(R.string.dialog_title_add_destination))
            .setPositiveButton(if (isEdit) getString(R.string.edit_button) else getString(R.string.dialog_add_button), null)
            .setNegativeButton("Cancel", null)
            .create()
        setUpListeners(dialog)

        return dialog
    }

    private fun setUpComponets() {
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
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)
                val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val dateString = format.format(selectedDate.time)
                datePickerEditText.setText(dateString)
            }, year, month, day).show()
        }
    }

    private fun setUpListeners(dialog: AlertDialog) {
        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                val destination = Destination(
                    destination?.id ?: generateTemporaryId(),
                    binding.dialogDestinationName.text.toString(),
                    binding.dialogDestinationDescription.text.toString(),
                    "",
                    binding.dialogSpinner.selectedItem.toString(),
                    binding.dialogDatePicker.text.toString(),
                    true
                )
                if (valiteInputs()) {
                    clickListener.onPositiveClick(destination)
                    dialog.dismiss()
                }else{
                    validateFields()
                }
            }

            val negativeButton = dialog.getButton(Dialog.BUTTON_NEGATIVE)
            negativeButton.setOnClickListener {
                dialog.dismiss()
            }
        }

        binding.dialogDestinationName.doAfterTextChanged {
            setErrorEditText(binding.dialogDestinationNameLayout)
        }
        binding.dialogDestinationDescription.doAfterTextChanged {
            setErrorEditText(binding.dialogDestinationDescriptionLayout)
        }
        binding.dialogDatePicker.doAfterTextChanged {
             setErrorEditText(binding.dialogDatePickerLayout)
        }
    }


    private fun valiteInputs(): Boolean {
        return binding.dialogDestinationName.text.toString().isNotEmpty() &&
                binding.dialogDestinationDescription.text.toString().isNotEmpty() &&
                binding.dialogDatePicker.text.toString().isNotEmpty()
    }
    private fun validateFields() {
        setErrorEditText(binding.dialogDestinationNameLayout)
        setErrorEditText(binding.dialogDestinationDescriptionLayout)
        setErrorEditText(binding.dialogDatePickerLayout)
    }

    private fun setErrorEditText(textInputLayout: TextInputLayout) {
        if(textInputLayout.editText?.text.toString().isEmpty()) {
            textInputLayout.error = getString(R.string.error_empty_field)
        }else{
            textInputLayout.error = null
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun generateTemporaryId(): Int {
        return Random.nextInt(Int.MAX_VALUE)
    }
    interface OnAddClickListener {
        fun onPositiveClick(destination: Destination)
    }
}