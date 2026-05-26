package com.nudi.nallanudi.ui

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.nudi.nallanudi.R
import com.nudi.nallanudi.data.Word
import com.nudi.nallanudi.databinding.ActivityDetailBinding
import com.nudi.nallanudi.viewmodel.WordViewModel
import java.util.Locale

class DetailActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var binding: ActivityDetailBinding
    private val viewModel: WordViewModel by viewModels()
    private lateinit var tts: TextToSpeech
    private var currentWord: Word? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize TTS
        tts = TextToSpeech(this, this)

        // Get word data from intent
        val englishTerm = intent.getStringExtra("english") ?: ""
        val kannadaTerm = intent.getStringExtra("kannada") ?: ""
        val explanation = intent.getStringExtra("explanation") ?: ""
        val example = intent.getStringExtra("example") ?: ""
        val subject = intent.getStringExtra("subject") ?: ""
        val isBookmarked = intent.getBooleanExtra("isBookmarked", false)
        val wordId = intent.getIntExtra("wordId", 0)

        currentWord = Word(
            id = wordId,
            englishTerm = englishTerm,
            kannadaTerm = kannadaTerm,
            explanation = explanation,
            example = example,
            subject = subject,
            isBookmarked = isBookmarked
        )

        // Populate UI
        binding.tvDetailEnglish.text = englishTerm
        binding.tvDetailKannada.text = kannadaTerm
        binding.tvDetailSubject.text = subject
        binding.tvDetailExplanation.text = explanation
        binding.tvDetailExample.text = example

        // Set bookmark icon
        updateBookmarkIcon(isBookmarked)

        // Back button
        binding.btnBack.setOnClickListener { finish() }

        // Speak button (English)
        binding.btnSpeak.setOnClickListener {
            speakWord(englishTerm, isKannada = false)
        }

        // Speak button (Kannada)
        binding.btnSpeakKannada.setOnClickListener {
            speakWord(kannadaTerm, isKannada = true)
        }

        // Bookmark button
        binding.btnBookmark.setOnClickListener {
            currentWord?.let { word ->
                viewModel.toggleBookmark(word)
                currentWord = word.copy(isBookmarked = !word.isBookmarked)
                updateBookmarkIcon(currentWord!!.isBookmarked)
                val msg = if (currentWord!!.isBookmarked) getString(R.string.added_to_list) else getString(R.string.removed_from_list)
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
        }

        // Share button
        binding.btnShare.setOnClickListener {
            shareWord()
        }

        // Copy button
        binding.btnCopy.setOnClickListener {
            copyToClipboard()
        }
    }

    private fun shareWord() {
        val word = currentWord ?: return
        val shareText = """
            *${word.englishTerm}* (${word.subject})
            Kannada: ${word.kannadaTerm}
            
            Explanation: ${word.explanation}
            
            Example: ${word.example}
            
            Shared via Nalla Nudi App
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        startActivity(Intent.createChooser(intent, "Share Term via"))
    }

    private fun copyToClipboard() {
        val word = currentWord ?: return
        val textToCopy = "${word.englishTerm}: ${word.kannadaTerm}\n${word.explanation}"
        val clipboard = getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("Nalla Nudi Term", textToCopy)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show()
    }

    private fun updateBookmarkIcon(isBookmarked: Boolean) {
        binding.btnBookmark.setImageResource(
            if (isBookmarked)
                android.R.drawable.btn_star_big_on
            else
                android.R.drawable.btn_star_big_off
        )
    }

    private fun speakWord(word: String, isKannada: Boolean) {
        if (::tts.isInitialized) {
            val locale = if (isKannada) Locale("kn", "IN") else Locale.US
            val result = tts.setLanguage(locale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                val lang = if (isKannada) "Kannada" else "English"
                Toast.makeText(this, "$lang voice data not found. Please enable it in device settings.", Toast.LENGTH_LONG).show()
            } else {
                tts.speak(word, TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.US
        } else {
            Toast.makeText(this, getString(R.string.tts_not_available), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        tts.stop()
        tts.shutdown()
        super.onDestroy()
    }
}