package com.univ.routeplanner.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.univ.routeplanner.data.db.AppDatabase
import com.univ.routeplanner.data.db.RouteEntity
import com.univ.routeplanner.data.repository.RouteRepository
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val repository: RouteRepository
) : ViewModel() {

    private val _routes = MutableLiveData<List<RouteEntity>>(emptyList())
    val routes: LiveData<List<RouteEntity>> = _routes

    private val _isEmpty = MutableLiveData<Boolean>(false)
    val isEmpty: LiveData<Boolean> = _isEmpty

    fun loadHistory() {
        viewModelScope.launch {
            val list = repository.getAllHistory()
            _routes.value = list
            _isEmpty.value = list.isEmpty()
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            repository.clearCache()
            loadHistory()  // refresh the list — should now be empty
        }
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val dao = AppDatabase.getInstance(context).routeDao()
            val repository = RouteRepository(dao = dao)
            @Suppress("UNCHECKED_CAST")
            return HistoryViewModel(repository) as T
        }
    }
}