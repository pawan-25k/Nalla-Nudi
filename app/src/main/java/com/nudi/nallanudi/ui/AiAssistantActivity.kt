package com.nudi.nallanudi.ui

import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.nudi.nallanudi.BuildConfig
import com.nudi.nallanudi.api.Content
import com.nudi.nallanudi.api.GeminiRequest
import com.nudi.nallanudi.api.Part
import com.nudi.nallanudi.api.RetrofitClient
import com.nudi.nallanudi.databinding.ActivityAiAssistantBinding
import kotlinx.coroutines.launch

class AiAssistantActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAiAssistantBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAiAssistantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        binding.btnSend.setOnClickListener {
            val query = binding.etQuery.text.toString().trim()
            if (query.isNotEmpty()) {
                askAi(query)
            }
        }
    }

    private fun askAi(query: String) {
        // Hide keyboard
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etQuery.windowToken, 0)

        binding.progressBar.visibility = View.VISIBLE
        binding.llResponseContainer.visibility = View.GONE
        binding.btnSend.isEnabled = false

        val systemPrompt = "You are Nalla Nudi AI, a helpful technical assistant for Kannada-medium students. " +
                "Your goal is to explain technical terms, words, and concepts in simple Kannada. " +
                "Always provide the English term, its Kannada equivalent, a clear explanation in Kannada, and an example sentence in Kannada. " +
                "User query: "

        val request = GeminiRequest(contents = listOf(Content(parts = listOf(Part(text = systemPrompt + query)))))

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.geminiService.generateContent(BuildConfig.GEMINI_API_KEY, request)
                val aiResponse = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "No response from AI."
                
                binding.tvQuestionTitle.text = query
                binding.tvAiResponse.text = aiResponse
                binding.llResponseContainer.visibility = View.VISIBLE
                binding.etQuery.text.clear()
                
                // Scroll to bottom
                binding.chatScrollView.post {
                    binding.chatScrollView.fullScroll(View.FOCUS_DOWN)
                }
            } catch (e: Exception) {
                val errorMsg = if (e is retrofit2.HttpException) {
                    "API Error (${e.code()}): ${e.message()}"
                } else {
                    "Error: ${e.message}"
                }
                Toast.makeText(this@AiAssistantActivity, errorMsg, Toast.LENGTH_LONG).show()
            } finally {
                binding.progressBar.visibility = View.GONE
                binding.btnSend.isEnabled = true
            }
        }
    }
}
