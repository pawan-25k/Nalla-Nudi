package com.nudi.nallanudi.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.nudi.nallanudi.BuildConfig
import com.nudi.nallanudi.api.Content
import com.nudi.nallanudi.api.GeminiRequest
import com.nudi.nallanudi.api.Part
import com.nudi.nallanudi.api.RetrofitClient
import com.nudi.nallanudi.data.Word
import com.nudi.nallanudi.databinding.ActivityAiQuizBinding
import com.nudi.nallanudi.viewmodel.WordViewModel
import kotlinx.coroutines.launch

class AiQuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAiQuizBinding
    private val viewModel: WordViewModel by viewModels()
    private var allWords: List<Word> = emptyList()
    private var score = 0
    private var correctAnswer: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAiQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        viewModel.searchResults.observe(this) { words ->
            if (words.isNotEmpty() && allWords.isEmpty()) {
                allWords = words
                generateAiQuestion()
            }
        }
        viewModel.searchWords("", "All")

        setupOptionListeners()
    }

    private fun generateAiQuestion() {
        if (allWords.isEmpty()) return

        val word = allWords.random()
        binding.progressBar.visibility = View.VISIBLE
        binding.tvAiQuestion.text = "Generating question for: ${word.englishTerm}..."
        setButtonsEnabled(false)

        val prompt = "Generate a multiple choice question in Kannada about the technical term '${word.englishTerm}' (Kannada: ${word.kannadaTerm}). " +
                "Provide 4 options in Kannada, with only one correct answer. " +
                "Format the response strictly as a JSON object: {\"question\": \"...\", \"options\": [\"...\", \"...\", \"...\", \"...\"], \"answer\": \"...\"}. " +
                "Do not include any other text."

        val request = GeminiRequest(contents = listOf(Content(parts = listOf(Part(text = prompt)))))

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.geminiService.generateContent(BuildConfig.GEMINI_API_KEY, request)
                val jsonText = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
                parseAndDisplayQuestion(jsonText)
            } catch (e: Exception) {
                Toast.makeText(this@AiQuizActivity, "AI Error: ${e.message}", Toast.LENGTH_LONG).show()
                // Fallback to local question if AI fails
                binding.tvAiQuestion.text = "Error generating AI question. Returning to safety."
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun parseAndDisplayQuestion(jsonText: String) {
        try {
            // Remove markdown code blocks if present
            val cleanedJson = jsonText.replace("```json", "").replace("```", "").trim()
            val quizData = Gson().fromJson(cleanedJson, QuizData::class.java)

            binding.tvAiQuestion.text = quizData.question
            val buttons = listOf(binding.btnAiOption1, binding.btnAiOption2, binding.btnAiOption3, binding.btnAiOption4)
            quizData.options.forEachIndexed { index, option ->
                if (index < buttons.size) {
                    buttons[index].text = option
                    buttons[index].visibility = View.VISIBLE
                }
            }
            correctAnswer = quizData.answer
            setButtonsEnabled(true)
        } catch (e: Exception) {
            Toast.makeText(this, "Parse Error: ${e.message}", Toast.LENGTH_SHORT).show()
            generateAiQuestion() // Try again
        }
    }

    private fun setupOptionListeners() {
        val buttons = listOf(binding.btnAiOption1, binding.btnAiOption2, binding.btnAiOption3, binding.btnAiOption4)
        buttons.forEach { button ->
            button.setOnClickListener {
                if (button.text == correctAnswer) {
                    score += 20
                    binding.tvAiScore.text = "Score: $score"
                    Toast.makeText(this, "Brilliant! AI is impressed. +20", Toast.LENGTH_SHORT).show()
                    generateAiQuestion()
                } else {
                    Toast.makeText(this, "Incorrect. AI expected: $correctAnswer", Toast.LENGTH_SHORT).show()
                    button.isEnabled = false
                    button.alpha = 0.5f
                }
            }
        }
    }

    private fun setButtonsEnabled(enabled: Boolean) {
        val buttons = listOf(binding.btnAiOption1, binding.btnAiOption2, binding.btnAiOption3, binding.btnAiOption4)
        buttons.forEach {
            it.isEnabled = enabled
            it.alpha = 1.0f
        }
    }

    data class QuizData(
        val question: String,
        val options: List<String>,
        val answer: String
    )
}
