package com.nudi.nallanudi.ui

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.nudi.nallanudi.data.Word
import com.nudi.nallanudi.databinding.ActivityQuizBinding
import com.nudi.nallanudi.viewmodel.WordViewModel

class QuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuizBinding
    private val viewModel: WordViewModel by viewModels()
    private var allWords: List<Word> = emptyList()
    private var currentQuestionWord: Word? = null
    private var score = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        viewModel.searchWords("", "All")
        viewModel.searchResults.observe(this) { words ->
            if (words.isNotEmpty()) {
                allWords = words
                if (currentQuestionWord == null) {
                    generateQuestion()
                }
            }
        }

        setupOptionListeners()
    }

    private fun generateQuestion() {
        if (allWords.size < 4) {
            Toast.makeText(this, "Not enough words for a quiz", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        currentQuestionWord = allWords.random()
        val options = mutableListOf<String>()
        options.add(currentQuestionWord!!.kannadaTerm)

        // Get 3 random wrong answers
        val wrongOptions = allWords.filter { it.id != currentQuestionWord!!.id }
            .shuffled()
            .take(3)
            .map { it.kannadaTerm }
        
        options.addAll(wrongOptions)
        options.shuffle()

        binding.tvQuestionWord.text = currentQuestionWord!!.englishTerm
        binding.btnOption1.text = options[0]
        binding.btnOption2.text = options[1]
        binding.btnOption3.text = options[2]
        binding.btnOption4.text = options[3]

        // Reset colors
        resetButtonStyles()
    }

    private fun setupOptionListeners() {
        val buttons = listOf(binding.btnOption1, binding.btnOption2, binding.btnOption3, binding.btnOption4)
        buttons.forEach { button ->
            button.setOnClickListener {
                checkAnswer(button)
            }
        }
    }

    private fun checkAnswer(selectedButton: Button) {
        val selectedAnswer = selectedButton.text.toString()
        val correct = selectedAnswer == currentQuestionWord?.kannadaTerm

        if (correct) {
            score += 10
            binding.tvScore.text = "Score: $score"
            Toast.makeText(this, "Correct! +10", Toast.LENGTH_SHORT).show()
            generateQuestion()
        } else {
            Toast.makeText(this, "Wrong! Try again", Toast.LENGTH_SHORT).show()
            selectedButton.isEnabled = false
            selectedButton.alpha = 0.5f
        }
    }

    private fun resetButtonStyles() {
        val buttons = listOf(binding.btnOption1, binding.btnOption2, binding.btnOption3, binding.btnOption4)
        buttons.forEach {
            it.isEnabled = true
            it.alpha = 1.0f
        }
    }
}