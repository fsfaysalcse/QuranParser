package Models

data class Verse(
    val chapter_id: Int,
    val id: Int,
    val verse_key: String,
    val page_number: String,
    val words: List<Word>
)