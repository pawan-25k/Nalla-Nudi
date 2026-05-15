package com.nudi.nallanudi.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nudi.nallanudi.data.Word
import com.nudi.nallanudi.data.WordDatabase
import com.nudi.nallanudi.data.WordRepository
import kotlinx.coroutines.launch

class WordViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WordRepository
    private val dao = WordDatabase.getDatabase(application).wordDao()

    private val _wordOfTheDay = MutableLiveData<Word?>()
    val wordOfTheDay: LiveData<Word?> = _wordOfTheDay

    private val _searchResults = MutableLiveData<List<Word>>()
    val searchResults: LiveData<List<Word>> = _searchResults

    private val _bookmarkedWords = MutableLiveData<List<Word>>()
    val bookmarkedWords: LiveData<List<Word>> = _bookmarkedWords

    init {
        repository = WordRepository(dao)
        seedAndLoad()
    }

    private fun seedAndLoad() {
        viewModelScope.launch {
            repository.seedDatabase(getApplication())
            _wordOfTheDay.postValue(repository.getWordOfTheDay())
        }
    }

    fun refreshWordOfTheDay() {
        viewModelScope.launch {
            _wordOfTheDay.postValue(repository.getWordOfTheDay())
        }
    }

    fun searchWords(query: String, subject: String = "All") {
        viewModelScope.launch {
            val results = if (query.isEmpty() && subject == "All") {
                repository.getAllWords()
            } else if (subject != "All" && query.isEmpty()) {
                repository.getWordsBySubject(subject)
            } else if (subject != "All") {
                repository.getWordsBySubject(subject).filter {
                    it.englishTerm.contains(query, ignoreCase = true)
                }
            } else {
                repository.searchWords(query)
            }
            _searchResults.postValue(results)
        }
    }

    fun toggleBookmark(word: Word) {
        viewModelScope.launch {
            repository.updateWord(word.copy(isBookmarked = !word.isBookmarked))
            loadBookmarks()
        }
    }

    fun loadBookmarks() {
        viewModelScope.launch {
            _bookmarkedWords.postValue(repository.getBookmarkedWords())
        }
    }
}