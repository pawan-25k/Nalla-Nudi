package com.nudi.nallanudi.data

class WordRepository(private val wordDao: WordDao) {

    suspend fun getAllWords() = wordDao.getAllWords()

    suspend fun searchWords(query: String) = wordDao.searchWords(query)

    suspend fun getWordsBySubject(subject: String) = wordDao.getWordsBySubject(subject)

    suspend fun getWordOfTheDay() = wordDao.getWordOfTheDay()

    suspend fun getBookmarkedWords() = wordDao.getBookmarkedWords()

    suspend fun updateWord(word: Word) = wordDao.updateWord(word)

    suspend fun seedDatabase(context: android.content.Context) {
        val db = WordDatabase.getDatabase(context)
        val existing = db.wordDao().getAllWords()
        // If count is less than our goal, clear and re-seed
        if (existing.size < 100) {
            db.wordDao().deleteAll() // Assuming deleteAll exists
            db.wordDao().insertAll(DataSeeder.getWords())
        }
    }
}