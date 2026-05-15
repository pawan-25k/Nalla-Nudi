package com.nudi.nallanudi.ui

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.nudi.nallanudi.databinding.ActivityFlashcardBinding
import com.nudi.nallanudi.data.Word
import com.nudi.nallanudi.viewmodel.WordViewModel

class FlashcardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFlashcardBinding
    private val viewModel: WordViewModel by viewModels()
    private var words: List<Word> = emptyList()
    private var currentIndex = 0
    private var isShowingFront = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlashcardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        // Load bookmarked words
        viewModel.loadBookmarks()
        viewModel.bookmarkedWords.observe(this) { bookmarked ->
            if (bookmarked.isNotEmpty()) {
                words = bookmarked
                showCard(currentIndex)
            }
        }

        // Flip card on tap
        binding.flashCard.setOnClickListener {
            flipCard()
        }

        // Previous card
        binding.btnPrev.setOnClickListener {
            if (currentIndex > 0) {
                currentIndex--
                isShowingFront = true
                showCard(currentIndex)
            }
        }

        // Next card
        binding.btnNext.setOnClickListener {
            if (currentIndex < words.size - 1) {
                currentIndex++
                isShowingFront = true
                showCard(currentIndex)
            }
        }
    }

    private fun showCard(index: Int) {
        if (words.isEmpty()) return
        val word = words[index]

        // Update progress
        binding.tvProgress.text = "Card ${index + 1} of ${words.size}"

        // Show front side
        binding.tvCardEnglish.text = word.englishTerm
        binding.tvCardKannada.text = word.kannadaTerm
        binding.tvCardExplanation.text = word.explanation

        // Reset to front
        isShowingFront = true
        binding.layoutFront.visibility = View.VISIBLE
        binding.layoutBack.visibility = View.GONE

        // Update button states
        binding.btnPrev.isEnabled = index > 0
        binding.btnNext.isEnabled = index < words.size - 1
        binding.btnPrev.alpha = if (index > 0) 1f else 0.4f
        binding.btnNext.alpha = if (index < words.size - 1) 1f else 0.4f
    }

    private fun flipCard() {
        val flipOut = android.view.animation.AnimationUtils.loadAnimation(this, android.R.anim.fade_out)
        val flipIn = android.view.animation.AnimationUtils.loadAnimation(this, android.R.anim.fade_in)

        binding.flashCard.startAnimation(flipOut)

        flipOut.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
            override fun onAnimationStart(animation: android.view.animation.Animation?) {}
            override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
            override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                if (isShowingFront) {
                    binding.layoutFront.visibility = View.GONE
                    binding.layoutBack.visibility = View.VISIBLE
                } else {
                    binding.layoutFront.visibility = View.VISIBLE
                    binding.layoutBack.visibility = View.GONE
                }
                isShowingFront = !isShowingFront
                binding.flashCard.startAnimation(flipIn)
            }
        })
    }
}