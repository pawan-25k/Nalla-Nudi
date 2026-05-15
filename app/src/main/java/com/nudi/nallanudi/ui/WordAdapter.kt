package com.nudi.nallanudi.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nudi.nallanudi.data.Word
import com.nudi.nallanudi.databinding.ItemWordBinding

class WordAdapter(
    private var words: List<Word>,
    private val onWordClick: (Word) -> Unit,
    private val onBookmarkClick: (Word) -> Unit
) : RecyclerView.Adapter<WordAdapter.WordViewHolder>() {

    inner class WordViewHolder(val binding: ItemWordBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val binding = ItemWordBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return WordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val word = words[position]
        with(holder.binding) {
            tvItemEnglish.text = word.englishTerm
            tvItemKannada.text = word.kannadaTerm
            tvItemSubject.text = word.subject

            // Bookmark icon
            btnBookmark.setImageResource(
                if (word.isBookmarked)
                    android.R.drawable.btn_star_big_on
                else
                    android.R.drawable.btn_star_big_off
            )

            root.setOnClickListener { onWordClick(word) }
            btnBookmark.setOnClickListener { onBookmarkClick(word) }
        }
    }

    override fun getItemCount() = words.size

    fun updateWords(newWords: List<Word>) {
        words = newWords
        notifyDataSetChanged()
    }
}