package ru.practicum.android.diploma.search.ui

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentSearchJobBinding
import ru.practicum.android.diploma.details.ui.JobDetailsFragment
import ru.practicum.android.diploma.search.domain.model.Vacancy
import ru.practicum.android.diploma.search.presentation.state.SearchFragmentState
import ru.practicum.android.diploma.search.presentation.viewmodel.SearchViewModel

class SearchJobFragment : Fragment() {
    private var _binding: FragmentSearchJobBinding? = null
    private val binding get() = _binding!!
    lateinit var adapter: VacancyAdapter
    private val viewModel by viewModel<SearchViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSearchJobBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewHolderInit()

        binding.searchFilterButton.setOnClickListener {
            findNavController().navigate(R.id.action_searchJobFragment_to_filterSettingsFragment)
        }

        binding.searchInput.doOnTextChanged { text, _, _, _ ->
            if (text.isNullOrEmpty()) {
                binding.searchInputIcon.background = requireActivity().getDrawable(R.drawable.icon_search)
            } else {
                binding.searchInputIcon.background = requireActivity().getDrawable(R.drawable.icon_cross)
            }
        }

        binding.searchInputIcon.setOnClickListener {
            binding.searchInput.setText("")
        }
        binding.searchInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.searchInput.showKeyboard()
                showView()
            }
        }
        binding.searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.searchInput.hideKeyboard()
            }
            false
        }

        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!p0.isNullOrEmpty()) {
                    viewModel.searchWithDebounce(p0.toString())
                    Log.d("поиск текст чнж ","поиск текст чнж")
                } else if (p0.isNullOrEmpty()) {
                    adapter.updateList(emptyList())
                    viewModel.updateState(SearchFragmentState.NoTextInInputEditText)
                    showView()
                    Log.d("текст чнж ", "пусто в эдит текст")
                }
            }
        })
        // Удалить потом!
        binding.searchTESTVACANCYButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_searchJobFragment_to_jobDetailsFragment,
                JobDetailsFragment.createArgs("linkToThisVacancy")
            )
        }
    }

    private fun showView() {
        viewModel.fragmentStateLiveData().observe(viewLifecycleOwner) {
            when (it) {
                is SearchFragmentState.SearchVacancy -> {
                    Log.d("списокShowView ", it.searchVacancy.toString())
                    adapter.updateList(it.searchVacancy)
                    searchVacancy()
                }

                is SearchFragmentState.Loading -> {
                    Log.d("шоу вью ", " Loading")
                    loading()
                }

                is SearchFragmentState.NoResult -> {
                    Log.d("шоу вью", "NoResult")
                    noResults()
                }

                is SearchFragmentState.ServerError -> {
                    Log.d("шоу вью ", "Error")
                    serverError()
                }

                is SearchFragmentState.NoTextInInputEditText -> {
                    Log.d("шоу вью ", "No text ")
                    noTextView()
                }

                else -> {}
            }
        }
    }

    private fun searchVacancy() {
        binding.recyclerViewSearch.visibility = View.VISIBLE
        binding.searchPlaceholderImage.visibility = View.GONE
    }

    private fun loading() {
        binding.recyclerViewSearch.visibility = View.GONE
        binding.searchPlaceholderImage.visibility = View.GONE
        binding.searchProgressBar.visibility = View.VISIBLE
    }

    private fun noTextView() {
        binding.recyclerViewSearch.visibility = View.GONE
        binding.searchPlaceholderImage.visibility = View.VISIBLE
        binding.searchProgressBar.visibility = View.GONE
    }

    private fun noResults() {}
    private fun serverError() {}

    private fun clickListenerFun() = object : SearchRecyclerViewEvent {
        override fun onItemClick(vacancy: Vacancy) {
            if (viewModel.clickDebounce()) {
                findNavController().navigate(
                    R.id.action_searchJobFragment_to_jobDetailsFragment,
                    JobDetailsFragment.createArgs(vacancy.id)
                )
            }
        }
    }

    private fun viewHolderInit() {
        adapter = VacancyAdapter(emptyList(), clickListenerFun())
        binding.recyclerViewSearch.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewSearch.adapter = adapter
    }

    private fun View.showKeyboard() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(this, 0)
    }

    private fun View.hideKeyboard() {
        val inputManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

    companion object {
        private const val VACANCY_ID = "vacancy_id"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



