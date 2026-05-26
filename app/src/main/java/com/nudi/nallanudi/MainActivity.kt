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

import android.speech.tts.TextToSpeech
import android.widget.Toast

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: WordViewModel by viewModels()
    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize TTS
        tts = TextToSpeech(this, this)

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

                binding.btnSpeakWOD.setOnClickListener { _ ->
                    speakWord(it.englishTerm)
                }
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

        // Flashcards button
        binding.btnGoToFlashcards.setOnClickListener {
            val intent = Intent(this, com.nudi.nallanudi.ui.FlashcardActivity::class.java)
            intent.putExtra("loadAll", true)
            startActivity(intent)
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

    private fun speakWord(word: String) {
        if (::tts.isInitialized) {
            tts.language = Locale.US
            tts.speak(word, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    override fun onInit(status: Int) {
        if (status != TextToSpeech.SUCCESS) {
            Toast.makeText(this, "TTS not available", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }
}