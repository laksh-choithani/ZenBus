package com.example.testing.ui.search

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.testing.R
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testing.databinding.FragmentAddrecordBinding
import com.example.testing.databinding.FragmentSearchBinding
import com.example.testing.model.APIModels.News
import com.example.testing.ui.addRecord.AddRecordViewModal
import kotlinx.coroutines.launch
import java.io.Serializable

class SearchFragment: Fragment() {
    var loading = false

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_search, container, false)
        val News = view.findViewById(R.id.searchNews) as RecyclerView

        val SearchModel: SearchViewModal =
            ViewModelProvider(requireActivity())[SearchViewModal::class.java]

        val adapter = SearchAdapter(
            SearchModel.newsList.value ?: ArrayList(0),
            ::onNewsClickHandler,
        )

        News.adapter = adapter
        News.layoutManager = LinearLayoutManager(view.context)
        if (SearchModel.newsList.value == null) {
            loadData(SearchModel, adapter)
        }

        val scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                if (SearchModel.canLoadMore && lastVisibleItemPosition == adapter.itemCount - 1) {
                    loadData(SearchModel, adapter)
                }
            }
        }
        News.addOnScrollListener(scrollListener)
        return view
    }
    private fun loadData(SearchModel : SearchViewModal, newsAdapter: SearchAdapter) {
        if (!loading) {
            loading = true
            lifecycleScope.launch {
                val newData = SearchModel.loadRecords()
                newsAdapter.addData(newData)
                Log.d("Laksh Kumar Choithani", newData.size.toString())
                loading = false
            }
        }
    }
    private fun onNewsClickHandler(news: News) {
        activity?.let {
            val intent = Intent(it, NewsActivity::class.java)
            intent.putExtra(
                "News",
                news as Serializable
            )
            it.startActivityFromFragment(this, intent, 1)
        }
    }
}