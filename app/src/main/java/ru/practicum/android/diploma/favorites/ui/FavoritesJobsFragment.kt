package ru.practicum.android.diploma.favorites.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFavoritesJobsBinding
import ru.practicum.android.diploma.favorites.presentation.state.FavoritesListState
import ru.practicum.android.diploma.favorites.presentation.viewmodel.FavoritesViewModel

class FavoritesJobsFragment : Fragment() {
    private val viewModel by viewModel<FavoritesViewModel>()
    private var _binding: FragmentFavoritesJobsBinding? = null
    private val binding: FragmentFavoritesJobsBinding
        get() {
            return _binding!!
        }
    private var adapter: VacancyDetailsRecyclerAdapter? = null
    private fun clickerForItem() =
        ClickerForVacancyDetail { vacancy -> viewModel.openForDetails(vacancyId = vacancy.id, findNavController()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesJobsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = VacancyDetailsRecyclerAdapter(emptyList(), clickerForItem())
        binding.favouritesJobsList.adapter = adapter
        binding.favouritesJobsList.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        viewModel.stateToObserve.observe(viewLifecycleOwner) { render(it) }
    }

    private fun render(state: FavoritesListState) {
        with(binding) {
            when (state) {
                FavoritesListState.Empty -> {
                    favouritesPlaceholderImage.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.picture_angry_cat
                        )
                    )
                    favouritesPlaceholderText.setText(R.string.list_is_empty)
                    favouritesPlaceholderImage.isVisible = true
                    favouritesPlaceholderText.isVisible = true
                }

                FavoritesListState.Error -> {
                    favouritesPlaceholderImage.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(), R.drawable.picture_checking_phone
                        )
                    )
                    favouritesPlaceholderText.setText(R.string.failed_list_vacancy)
                    favouritesPlaceholderImage.isVisible = true
                    favouritesPlaceholderText.isVisible = true
                }

                is FavoritesListState.Success -> {
                    adapter?.vacancies = state.listOfFavVacancies
                    favouritesPlaceholderText.isVisible = false
                    favouritesPlaceholderImage.isVisible = false
                    favouritesJobsList.isVisible = true
                }
            }
        }
    }
}
