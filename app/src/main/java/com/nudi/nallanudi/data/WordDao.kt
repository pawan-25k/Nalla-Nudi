package com.nudi.nallanudi.data

import androidx.room.*

@Dao
interface WordDao {

    @Query("SELECT * FROM words")
    suspend fun getAllWords(): List<Word>

    @Query("SELECT * FROM words WHERE englishTerm LIKE '%' || :query || '%'")
    suspend fun searchWords(query: String): List<Word>

    @Query("SELECT * FROM words WHERE subject = :subject")
    suspend fun getWordsBySubject(subject: String): List<Word>

    @Query("SELECT * FROM words ORDER BY RANDOM() LIMIT 1")
    suspend fun getWordOfTheDay(): Word?

    @Query("SELECT * FROM words WHERE isBookmarked = 1")
    suspend fun getBookmarkedWords(): List<Word>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(words: List<Word>)

    @Update
    suspend fun updateWord(word: Word)

    @Query("DELETE FROM words")
    suspend fun deleteAll()
}