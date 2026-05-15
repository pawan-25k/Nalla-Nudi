package com.nudi.nallanudi

import com.nudi.nallanudi.ui.MyListActivity
import com.nudi.nallanudi.ui.SearchActivity
import com.nudi.nallanudi.ui.QuizActivity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.nudi.nallanudi.databinding.ActivityMainBinding
import com.nudi.nallanudi.viewmodel.WordViewModel

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: WordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set Language Switch Initial State
        val currentLocales = AppCompatDelegate.getApplicationLocales()
        binding.switchLanguage.isChecked = currentLocales.get(0)?.language == "kn"

        binding.switchLanguage.setOnCheckedChangeListener { _, isChecked ->
            val appLocale: LocaleListCompat = if (isChecked) {
                LocaleListCompat.forLanguageTags("kn")
            } else {
                LocaleListCompat.forLanguageTags("en")
            }
            AppCompatDelegate.setApplicationLocales(appLocale)
        }

        // Set Today's Date
        val dateFormat = SimpleDateFormat("EEEE, dd MMM", Locale.getDefault())
        binding.tvDate.text = dateFormat.format(Date())

        // Observe Word of the Day
        viewModel.wordOfTheDay.observe(this) { word ->
            word?.let {
                binding.tvEnglishWord.text = it.englishTerm
                binding.tvKannadaWord.text = it.kannadaTerm
                binding.tvExample.text = it.example
            }
        }

        // Search buttons
        val goToSearch = { startActivity(Intent(this, SearchActivity::class.java)) }
        binding.btnGoToSearch.setOnClickListener { goToSearch() }
        binding.btnMainSearch.setOnClickListener { goToSearch() }

        // My List button
        binding.btnGoToMyList.setOnClickListener {
            startActivity(Intent(this, MyListActivity::class.java))
        }

        // Quiz button
        binding.btnGoToQuiz.setOnClickListener {
            startActivity(Intent(this, QuizActivity::class.java))
        }

        // AI Assistant button
        binding.btnGoToAiAssistant.setOnClickListener {
            startActivity(Intent(this, com.nudi.nallanudi.ui.AiAssistantActivity::class.java))
        }

        // Bottom Navigation Logic
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_search -> {
                    startActivity(Intent(this, SearchActivity::class.java))
                    false
                }
                R.id.nav_list -> {
                    startActivity(Intent(this, MyListActivity::class.java))
                    false
                }
                R.id.nav_quiz -> {
                    startActivity(Intent(this, QuizActivity::class.java))
                    false
                }
                else -> false
            }
        }
    }
}