package ru.practicum.android.diploma.filter.presentation

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFilterSettingsBinding
import ru.practicum.android.diploma.filter.presentation.state.FilterSettingsState
import ru.practicum.android.diploma.filter.presentation.viewmodel.FilterSettingsViewModel
import ru.practicum.android.diploma.search.ui.SearchRepeatHandler
import ru.practicum.android.diploma.utils.debounce

private const val SALARY_ITEMS_DEBOUNCE_DELAY = 100L

class FilterSettingsFragment : Fragment() {
    private var _binding: FragmentFilterSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<FilterSettingsViewModel>()
    private var previousSalaryText = String()
    private var previousCheckBoxValue = false
    private var salaryGotFocused = false
    private var checkBoxDebounced: ((Boolean) -> Unit)? = null
    private var salaryTextDebounced: ((String) -> Unit)? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFilterSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateAllFiltersInfo()
    }

    private fun initiateDebounce() {
        checkBoxDebounced = debounce(
            SALARY_ITEMS_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, true
        ) { doHideNoSalaryVacs ->
            if (doHideNoSalaryVacs != previousCheckBoxValue) {
                viewModel.changeHideNoSalary(doHideNoSalaryVacs)
                previousCheckBoxValue = doHideNoSalaryVacs
            }
        }

        salaryTextDebounced = debounce(
            SALARY_ITEMS_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, true
        ) { salary ->
            setSalary(salary)
        }
    }

    private fun setClicks() {
        binding.filterArrowBack.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.filterWorkPlaceInactive.setOnClickListener {
            findNavController().navigate(R.id.action_filterSettingsFragment_to_filterPlaceToWorkFragment)
        }

        binding.filterIndustryInactive.setOnClickListener {
            findNavController().navigate(R.id.action_filterSettingsFragment_to_filterIndustryFragment)
        }

        binding.filterWorkPlaceCross.setOnClickListener {
            viewModel.resetRegion()
        }

        binding.filterIndustryCross.setOnClickListener {
            viewModel.resetIndustry()
        }
        binding.filterResetButton.setOnClickListener {
            viewModel.resetFilterSettings()
        }

        binding.filterApplyButton.setOnClickListener {
            viewModel.saveFilterSettings()
            doRepeatBoolSequence()
            findNavController().navigateUp()
        }
        binding.filterSalaryCross.setOnClickListener {
            viewModel.changeSalary(String())
            binding.filterSalaryInput.setText(String())
        }
    }

    private fun setSalary(salary: String) {
        if (salary != previousSalaryText) {
            viewModel.changeSalary(salary)
            previousSalaryText = salary
        }
    }

    private fun doRepeatBoolSequence() {
        val repeatHandler = requireActivity()
        if (repeatHandler is SearchRepeatHandler) {
            repeatHandler.setRepeat(true)
        }
    }

    private fun setTextActions() {
        val salaryTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                TODO("Not yet implemented")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    binding.filterSalaryCross.visibility = View.GONE
                    binding.filterSalaryInputTitle.setTextColor(requireActivity().getColor(R.color.Black))
                    if (binding.filterSalaryInput.hasFocus()) {
                        binding.filterSalaryInputTitle.setTextColor(requireActivity().getColor(R.color.Blue))
                    }
                } else {
                    binding.filterSalaryCross.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {
                salaryTextDebounced?.invoke(s.toString())
            }
        }

        binding.filterSalaryInput.addTextChangedListener(salaryTextWatcher)

        binding.filterSalaryInput.doOnTextChanged { text, _, _, _ ->

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getState().observe(viewLifecycleOwner) { state ->
            render(state)
        }
        setClicks()
        setTextActions()
        initiateDebounce()
        binding.filterDontShowWithoutSalaryCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (salaryGotFocused) setSalary(binding.filterSalaryInput.text.toString())
            checkBoxDebounced?.invoke(isChecked)
        }
    }

    private fun render(state: FilterSettingsState) {
        when (state) {
            is FilterSettingsState.Data -> {
                binding.filterApplyButton.isVisible = state.isActiveApply
                binding.filterResetButton.isVisible = state.isActiveReset
                binding.filterDontShowWithoutSalaryCheckBox.isChecked = state.filter.hideNoSalaryItems == true
                if (state.filter.expectedSalary.isNullOrEmpty()) {
                    binding.filterSalaryInput.text.clear()
                    binding.filterSalaryInputTitle.setTextColor(requireActivity().getColor(R.color.Gray_OR_White))
                } else {
                    binding.filterSalaryInput.setText(state.filter.expectedSalary)
                    binding.filterSalaryInput.setSelection(state.filter.expectedSalary.count())
                    binding.filterSalaryInputTitle.setTextColor(requireActivity().getColor(R.color.Black))
                }

                if (state.filter.country?.countryName.isNullOrEmpty()) {
                    binding.filterWorkPlaceInactive.visibility = View.VISIBLE
                    binding.filterWorkPlaceActive.visibility = View.GONE
                } else {
                    binding.filterWorkPlaceInactive.visibility = View.GONE
                    binding.filterWorkPlaceActive.visibility = View.VISIBLE
                    binding.filterWorkPlaceValue.text = if (state.filter.area?.areaName == null) {
                        state.filter.country?.countryName
                    } else {
                        requireActivity().getString(
                            R.string.filter_region,
                            state.filter.country?.countryName,
                            state.filter.area.areaName
                        )
                    }
                }

                if (state.filter.industry?.industryName.isNullOrEmpty()) {
                    binding.filterIndustryInactive.visibility = View.VISIBLE
                    binding.filterIndustryActive.visibility = View.GONE
                } else {
                    binding.filterIndustryInactive.visibility = View.GONE
                    binding.filterIndustryActive.visibility = View.VISIBLE
                    binding.filterIndustryValue.text = state.filter.industry!!.industryName
                }
            }

            else -> {
                findNavController().navigateUp()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
