package com.nudi.nallanudi.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.nudi.nallanudi.data.Word
import com.nudi.nallanudi.databinding.ActivityMylistBinding
import com.nudi.nallanudi.viewmodel.WordViewModel

class MyListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMylistBinding
    private val viewModel: WordViewModel by viewModels()
    private lateinit var adapter: WordAdapter
    private var bookmarkedWords: List<Word> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMylistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        binding.btnBack.setOnClickListener { finish() }

        binding.btnFlashcard.setOnClickListener {
            if (bookmarkedWords.isEmpty()) return@setOnClickListener
            val intent = Intent(this, FlashcardActivity::class.java)
            val ids = bookmarkedWords.map { it.id }.toIntArray()
            intent.putExtra("wordIds", ids)
            startActivity(intent)
        }

        viewModel.loadBookmarks()
        viewModel.bookmarkedWords.observe(this) { words ->
            bookmarkedWords = words
            adapter.updateWords(words)
            binding.tvWordCount.text = "${words.size} saved words"
            if (words.isEmpty()) {
                binding.layoutEmpty.visibility = View.VISIBLE
                binding.recyclerMyList.visibility = View.GONE
                binding.tvWordCount.visibility = View.GONE
            } else {
                binding.layoutEmpty.visibility = View.GONE
                binding.recyclerMyList.visibility = View.VISIBLE
                binding.tvWordCount.visibility = View.VISIBLE
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = WordAdapter(
            words = emptyList(),
            onWordClick = { word ->
                val intent = Intent(this, DetailActivity::class.java).apply {
                    putExtra("wordId", word.id)
                    putExtra("english", word.englishTerm)
                    putExtra("kannada", word.kannadaTerm)
                    putExtra("explanation", word.explanation)
                    putExtra("example", word.example)
                    putExtra("subject", word.subject)
                    putExtra("isBookmarked", word.isBookmarked)
                }
                startActivity(intent)
            },
            onBookmarkClick = { word ->
                viewModel.toggleBookmark(word)
            }
        )
        binding.recyclerMyList.layoutManager = LinearLayoutManager(this)
        binding.recyclerMyList.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadBookmarks()
    }
}